package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.TraceDTO;
import com.herb.platform.dto.TraceNodeDTO;
import com.herb.platform.entity.Crop;
import com.herb.platform.entity.Trace;
import com.herb.platform.entity.TraceNode;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.TraceMapper;
import com.herb.platform.mapper.TraceNodeMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.TraceService;
import com.herb.platform.utils.QRCodeUtil;
import com.herb.platform.vo.TraceNodeVO;
import com.herb.platform.vo.TraceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 溯源服务实现类
 */
@Service
@RequiredArgsConstructor
public class TraceServiceImpl implements TraceService {

    private final TraceMapper traceMapper;
    private final TraceNodeMapper traceNodeMapper;
    private final CropMapper cropMapper;
    private final UserMapper userMapper;
    private final QRCodeUtil qrCodeUtil;

    @Override
    public IPage<TraceVO> page(Long userId, Long cropId, String herbName, Integer status, int pageNum, int pageSize) {
        Page<Trace> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Trace> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Trace::getUserId, userId);
        }
        if (cropId != null) {
            wrapper.eq(Trace::getCropId, cropId);
        }
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Trace::getHerbName, herbName);
        }
        if (status != null) {
            wrapper.eq(Trace::getStatus, status);
        }
        wrapper.orderByDesc(Trace::getCreateTime);

        IPage<Trace> tracePage = traceMapper.selectPage(page, wrapper);

        return tracePage.convert(this::convertToVO);
    }

    @Override
    public TraceVO getById(Long id) {
        Trace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVOWithNodes(trace);
    }

    @Override
    public TraceVO getByTraceCode(String traceCode) {
        LambdaQueryWrapper<Trace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trace::getTraceCode, traceCode);
        wrapper.eq(Trace::getStatus, 1); // 只查询已发布的
        Trace trace = traceMapper.selectOne(wrapper);
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "溯源信息不存在或未发布");
        }
        // 增加扫码次数
        incrementScanCount(trace.getId());
        return convertToVOWithNodes(trace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long userId, TraceDTO dto) {
        // 验证作物是否存在
        if (dto.getCropId() != null) {
            Crop crop = cropMapper.selectById(dto.getCropId());
            if (crop == null) {
                throw new BusinessException("作物不存在");
            }
            if (!crop.getUserId().equals(userId)) {
                throw new BusinessException(ResponseCode.FORBIDDEN, "无权为此作物创建溯源信息");
            }
        }

        Trace trace = new Trace();
        BeanUtils.copyProperties(dto, trace);
        trace.setUserId(userId);
        trace.setTraceCode(generateTraceCode());
        trace.setStatus(0); // 草稿状态
        trace.setScanCount(0);
        traceMapper.insert(trace);

        return trace.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, TraceDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "溯源ID不能为空");
        }

        Trace trace = traceMapper.selectById(dto.getId());
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此溯源信息");
        }
        if (trace.getStatus() == 1) {
            throw new BusinessException("已发布的溯源信息不能修改");
        }

        BeanUtils.copyProperties(dto, trace, "id", "userId", "traceCode", "status", "scanCount", "qrCodeUrl", "createTime");
        traceMapper.updateById(trace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Trace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此溯源信息");
        }

        // 删除关联的溯源节点
        LambdaQueryWrapper<TraceNode> nodeWrapper = new LambdaQueryWrapper<>();
        nodeWrapper.eq(TraceNode::getTraceId, id);
        traceNodeMapper.delete(nodeWrapper);

        // 删除溯源信息
        traceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long userId, Long id) {
        Trace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此溯源信息");
        }
        if (trace.getStatus() == 1) {
            throw new BusinessException("溯源信息已发布");
        }

        // 生成二维码
        String qrCodeUrl = generateQRCode(id);
        trace.setQrCodeUrl(qrCodeUrl);
        trace.setStatus(1);
        traceMapper.updateById(trace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNode(Long userId, TraceNodeDTO dto) {
        // 验证溯源信息是否存在
        Trace trace = traceMapper.selectById(dto.getTraceId());
        if (trace == null) {
            throw new BusinessException("溯源信息不存在");
        }
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此溯源信息");
        }

        TraceNode node = new TraceNode();
        BeanUtils.copyProperties(dto, node);
        node.setOperatorId(userId);

        // 获取操作人名称
        User user = userMapper.selectById(userId);
        if (user != null) {
            node.setOperator(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        // 设置排序
        if (node.getSortOrder() == null) {
            LambdaQueryWrapper<TraceNode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TraceNode::getTraceId, dto.getTraceId());
            wrapper.orderByDesc(TraceNode::getSortOrder);
            wrapper.last("LIMIT 1");
            TraceNode lastNode = traceNodeMapper.selectOne(wrapper);
            node.setSortOrder(lastNode == null ? 1 : lastNode.getSortOrder() + 1);
        }

        traceNodeMapper.insert(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNode(Long userId, TraceNodeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "节点ID不能为空");
        }

        TraceNode node = traceNodeMapper.selectById(dto.getId());
        if (node == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        // 验证权限
        Trace trace = traceMapper.selectById(node.getTraceId());
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此节点");
        }

        BeanUtils.copyProperties(dto, node, "id", "traceId", "operatorId", "operator", "createTime");
        traceNodeMapper.updateById(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(Long userId, Long nodeId) {
        TraceNode node = traceNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        // 验证权限
        Trace trace = traceMapper.selectById(node.getTraceId());
        if (!trace.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此节点");
        }

        traceNodeMapper.deleteById(nodeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementScanCount(Long traceId) {
        Trace trace = traceMapper.selectById(traceId);
        if (trace != null) {
            trace.setScanCount(trace.getScanCount() + 1);
            traceMapper.updateById(trace);
        }
    }

    @Override
    public String generateQRCode(Long traceId) {
        Trace trace = traceMapper.selectById(traceId);
        if (trace == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        // 二维码内容为溯源查询URL
        String content = "https://herb.example.com/trace/" + trace.getTraceCode();
        return qrCodeUtil.generateQRCode(content);
    }

    /**
     * 生成溯源码
     */
    private String generateTraceCode() {
        return "TR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 转换为VO（不含节点）
     */
    private TraceVO convertToVO(Trace trace) {
        TraceVO vo = new TraceVO();
        BeanUtils.copyProperties(trace, vo);
        vo.setStatusName(getStatusName(trace.getStatus()));

        // 获取作物名称
        if (trace.getCropId() != null) {
            Crop crop = cropMapper.selectById(trace.getCropId());
            if (crop != null) {
                vo.setCropName(crop.getCropName());
            }
        }

        // 获取用户名
        User user = userMapper.selectById(trace.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }

        return vo;
    }

    /**
     * 转换为VO（含节点）
     */
    private TraceVO convertToVOWithNodes(Trace trace) {
        TraceVO vo = convertToVO(trace);

        // 获取溯源节点
        LambdaQueryWrapper<TraceNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceNode::getTraceId, trace.getId());
        wrapper.orderByAsc(TraceNode::getSortOrder);
        List<TraceNode> nodes = traceNodeMapper.selectList(wrapper);

        List<TraceNodeVO> nodeVOs = nodes.stream().map(node -> {
            TraceNodeVO nodeVO = new TraceNodeVO();
            BeanUtils.copyProperties(node, nodeVO);
            nodeVO.setNodeTypeName(getNodeTypeName(node.getNodeType()));
            return nodeVO;
        }).collect(Collectors.toList());

        vo.setNodes(nodeVOs);

        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发布";
            default: return "";
        }
    }

    private String getNodeTypeName(String nodeType) {
        if (nodeType == null) return "";
        switch (nodeType) {
            case "plant": return "种植";
            case "grow": return "生长";
            case "fertilize": return "施肥";
            case "spray": return "施药";
            case "harvest": return "采收";
            case "process": return "加工";
            case "quality": return "质检";
            case "package": return "包装";
            case "storage": return "仓储";
            case "transport": return "运输";
            default: return nodeType;
        }
    }
}
