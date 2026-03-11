package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.entity.Crop;
import com.herb.platform.entity.HerbCalendarStage;
import com.herb.platform.entity.HerbDisease;
import com.herb.platform.entity.HerbKnowledge;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.HerbCalendarStageMapper;
import com.herb.platform.mapper.HerbDiseaseMapper;
import com.herb.platform.mapper.HerbKnowledgeMapper;
import com.herb.platform.service.KnowledgeService;
import com.herb.platform.utils.ImageFeatureUtil;
import com.herb.platform.vo.CalendarReminderVO;
import com.herb.platform.vo.DiseaseRecognitionVO;
import com.herb.platform.vo.HerbCalendarStageVO;
import com.herb.platform.vo.HerbCalendarVO;
import com.herb.platform.vo.HerbDiseaseVO;
import com.herb.platform.vo.HerbKnowledgeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Knowledge service implementation.
 */
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final HerbKnowledgeMapper herbKnowledgeMapper;
    private final HerbDiseaseMapper herbDiseaseMapper;
    private final HerbCalendarStageMapper herbCalendarStageMapper;
    private final CropMapper cropMapper;

    private final Map<String, double[]> imageFeatureCache = new ConcurrentHashMap<>();

    @Override
    public IPage<HerbKnowledgeVO> pageKnowledge(String keyword, String herbCategory, String plantingSeason, String diseaseType, int pageNum, int pageSize) {
        Page<HerbKnowledge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HerbKnowledge> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(HerbKnowledge::getHerbName, keyword)
                    .or().like(HerbKnowledge::getHerbAlias, keyword)
                    .or().like(HerbKnowledge::getKeywordTags, keyword)
                    .or().like(HerbKnowledge::getSummary, keyword)
                    .or().like(HerbKnowledge::getDiseasePrevention, keyword));
        }
        if (StringUtils.hasText(herbCategory)) {
            wrapper.eq(HerbKnowledge::getHerbCategory, herbCategory);
        }
        if (StringUtils.hasText(plantingSeason)) {
            wrapper.eq(HerbKnowledge::getPlantingSeason, plantingSeason);
        }
        if (StringUtils.hasText(diseaseType)) {
            wrapper.eq(HerbKnowledge::getDiseaseType, diseaseType);
        }
        wrapper.orderByAsc(HerbKnowledge::getHerbCategory);
        wrapper.orderByAsc(HerbKnowledge::getHerbName);
        return herbKnowledgeMapper.selectPage(page, wrapper).convert(this::toKnowledgeVO);
    }

    @Override
    public List<HerbDiseaseVO> listDiseases(String herbName, String diseaseType, String keyword) {
        LambdaQueryWrapper<HerbDisease> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(herbName)) {
            wrapper.eq(HerbDisease::getHerbName, herbName);
        }
        if (StringUtils.hasText(diseaseType)) {
            wrapper.eq(HerbDisease::getDiseaseType, diseaseType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(HerbDisease::getDiseaseName, keyword)
                    .or().like(HerbDisease::getSymptomKeywords, keyword)
                    .or().like(HerbDisease::getSymptomDescription, keyword));
        }
        wrapper.orderByAsc(HerbDisease::getHerbName);
        wrapper.orderByAsc(HerbDisease::getDiseaseName);
        return herbDiseaseMapper.selectList(wrapper).stream()
                .map(this::toDiseaseVO)
                .collect(Collectors.toList());
    }

    @Override
    public DiseaseRecognitionVO identifyDisease(String herbName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "请上传病害图片");
        }

        List<HerbDisease> candidates = herbDiseaseMapper.selectList(new LambdaQueryWrapper<HerbDisease>()
                .eq(StringUtils.hasText(herbName), HerbDisease::getHerbName, herbName)
                .orderByAsc(HerbDisease::getHerbName)
                .orderByAsc(HerbDisease::getDiseaseName));
        if (candidates.isEmpty()) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "未找到可匹配的病害样本");
        }

        double[] uploadedFeature;
        try (InputStream inputStream = file.getInputStream()) {
            uploadedFeature = ImageFeatureUtil.extractFeature(inputStream);
        } catch (Exception e) {
            throw new BusinessException("识别图片处理失败: " + e.getMessage());
        }

        List<HerbDiseaseVO> matches = candidates.stream()
                .map(disease -> {
                    HerbDiseaseVO vo = toDiseaseVO(disease);
                    double similarity = ImageFeatureUtil.cosineSimilarity(uploadedFeature, getDiseaseFeature(disease.getImageUrl()));
                    vo.setMatchScore(Math.round(similarity * 1000D) / 10D);
                    vo.setMatchReason(buildMatchReason(disease, similarity));
                    return vo;
                })
                .sorted(Comparator.comparing(HerbDiseaseVO::getMatchScore).reversed())
                .limit(3)
                .collect(Collectors.toList());

        DiseaseRecognitionVO result = new DiseaseRecognitionVO();
        result.setFileName(file.getOriginalFilename());
        result.setHerbName(StringUtils.hasText(herbName) ? herbName : matches.get(0).getHerbName());
        result.setMatches(matches);
        return result;
    }

    @Override
    public HerbCalendarVO getCalendar(Long userId, Long cropId, String herbName, LocalDate plantDate) {
        Crop crop = null;
        if (cropId != null) {
            crop = cropMapper.selectById(cropId);
            if (crop == null) {
                throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "作物不存在");
            }
            if (userId != null && crop.getUserId() != null && !crop.getUserId().equals(userId)) {
                throw new BusinessException(ResponseCode.FORBIDDEN, "无权查看该作物日历");
            }
            herbName = crop.getCropName();
            plantDate = crop.getPlantDate();
        }

        if (!StringUtils.hasText(herbName)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "请先选择药材或作物");
        }
        if (plantDate == null) {
            plantDate = LocalDate.now();
        }

        List<HerbCalendarStage> templates = herbCalendarStageMapper.selectList(new LambdaQueryWrapper<HerbCalendarStage>()
                .eq(HerbCalendarStage::getHerbName, herbName)
                .orderByAsc(HerbCalendarStage::getSortOrder));
        if (templates.isEmpty()) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "该药材暂无日历模板");
        }

        HerbCalendarVO calendarVO = new HerbCalendarVO();
        calendarVO.setCropId(crop == null ? null : crop.getId());
        calendarVO.setHerbName(herbName);
        calendarVO.setPlantDate(plantDate);

        List<HerbCalendarStageVO> stages = buildStageVOs(templates, plantDate);
        calendarVO.setStages(stages);
        calendarVO.setExpectedHarvestDate(stages.get(stages.size() - 1).getEndDate());
        calendarVO.setReminders(buildRemindersForSingleCrop(crop, herbName, stages));
        return calendarVO;
    }

    @Override
    public List<CalendarReminderVO> getUpcomingReminders(Long userId, int days) {
        if (userId == null) {
            return new ArrayList<>();
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        List<Crop> crops = cropMapper.selectList(new LambdaQueryWrapper<Crop>()
                .eq(Crop::getUserId, userId)
                .orderByDesc(Crop::getPlantDate));

        List<CalendarReminderVO> reminders = new ArrayList<>();
        for (Crop crop : crops) {
            if (!StringUtils.hasText(crop.getCropName()) || crop.getPlantDate() == null) {
                continue;
            }
            List<HerbCalendarStage> stages = herbCalendarStageMapper.selectList(new LambdaQueryWrapper<HerbCalendarStage>()
                    .eq(HerbCalendarStage::getHerbName, crop.getCropName())
                    .orderByAsc(HerbCalendarStage::getSortOrder));
            if (stages.isEmpty()) {
                continue;
            }
            for (HerbCalendarStage stage : stages) {
                LocalDate reminderDate = crop.getPlantDate().plusDays(stage.getDayOffset() - safeInt(stage.getReminderDays()));
                if ((reminderDate.isEqual(today) || reminderDate.isAfter(today)) && !reminderDate.isAfter(endDate)) {
                    CalendarReminderVO reminder = new CalendarReminderVO();
                    reminder.setCropId(crop.getId());
                    reminder.setCropName(crop.getCropName());
                    reminder.setHerbName(crop.getCropName());
                    reminder.setStageName(stage.getStageName());
                    reminder.setActionType(stage.getActionType());
                    reminder.setReminderDate(reminderDate);
                    reminder.setDaysUntil(ChronoUnit.DAYS.between(today, reminderDate));
                    reminder.setStageTips(stage.getStageTips());
                    reminders.add(reminder);
                }
            }
        }
        reminders.sort(Comparator.comparing(CalendarReminderVO::getReminderDate));
        return reminders;
    }

    private HerbKnowledgeVO toKnowledgeVO(HerbKnowledge knowledge) {
        HerbKnowledgeVO vo = new HerbKnowledgeVO();
        BeanUtils.copyProperties(knowledge, vo);
        return vo;
    }

    private HerbDiseaseVO toDiseaseVO(HerbDisease disease) {
        HerbDiseaseVO vo = new HerbDiseaseVO();
        BeanUtils.copyProperties(disease, vo);
        return vo;
    }

    private List<HerbCalendarStageVO> buildStageVOs(List<HerbCalendarStage> templates, LocalDate plantDate) {
        List<HerbCalendarStageVO> stageVOs = new ArrayList<>();
        for (HerbCalendarStage template : templates) {
            HerbCalendarStageVO stageVO = new HerbCalendarStageVO();
            stageVO.setStageName(template.getStageName());
            stageVO.setActionType(template.getActionType());
            LocalDate startDate = plantDate.plusDays(safeInt(template.getDayOffset()));
            LocalDate endDate = startDate.plusDays(Math.max(0, safeInt(template.getDurationDays()) - 1L));
            stageVO.setStartDate(startDate);
            stageVO.setEndDate(endDate);
            stageVO.setReminderDate(startDate.minusDays(safeInt(template.getReminderDays())));
            stageVO.setOperationWindow(template.getOperationWindow());
            stageVO.setStageTips(template.getStageTips());
            stageVO.setSortOrder(template.getSortOrder());
            stageVOs.add(stageVO);
        }
        return stageVOs;
    }

    private List<CalendarReminderVO> buildRemindersForSingleCrop(Crop crop, String herbName, List<HerbCalendarStageVO> stages) {
        LocalDate today = LocalDate.now();
        return stages.stream()
                .map(stage -> {
                    CalendarReminderVO reminder = new CalendarReminderVO();
                    reminder.setCropId(crop == null ? null : crop.getId());
                    reminder.setCropName(crop == null ? herbName : crop.getCropName());
                    reminder.setHerbName(herbName);
                    reminder.setStageName(stage.getStageName());
                    reminder.setActionType(stage.getActionType());
                    reminder.setReminderDate(stage.getReminderDate());
                    reminder.setDaysUntil(ChronoUnit.DAYS.between(today, stage.getReminderDate()));
                    reminder.setStageTips(stage.getStageTips());
                    return reminder;
                })
                .filter(reminder -> reminder.getDaysUntil() >= -3 && reminder.getDaysUntil() <= 15)
                .sorted(Comparator.comparing(CalendarReminderVO::getReminderDate))
                .collect(Collectors.toList());
    }

    private double[] getDiseaseFeature(String imageUrl) {
        return imageFeatureCache.computeIfAbsent(imageUrl, key -> {
            String resourcePath = toResourcePath(key);
            try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
                return ImageFeatureUtil.extractFeature(inputStream);
            } catch (Exception e) {
                throw new BusinessException("病害样本加载失败: " + resourcePath);
            }
        });
    }

    private String toResourcePath(String imageUrl) {
        String normalized = imageUrl == null ? "" : imageUrl.trim();
        if (normalized.startsWith("/api/static/")) {
            return "static/" + normalized.substring("/api/static/".length());
        }
        if (normalized.startsWith("/static/")) {
            return "static/" + normalized.substring("/static/".length());
        }
        if (normalized.startsWith("static/")) {
            return normalized;
        }
        throw new BusinessException("样本图片路径不合法: " + imageUrl);
    }

    private String buildMatchReason(HerbDisease disease, double similarity) {
        String level;
        if (similarity >= 0.95D) {
            level = "与样本图高度相似";
        } else if (similarity >= 0.85D) {
            level = "病斑颜色与分布较为接近";
        } else {
            level = "建议结合症状描述进一步人工复核";
        }
        return level + "，可优先参考“" + disease.getDiseaseName() + "”的防治方案";
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
