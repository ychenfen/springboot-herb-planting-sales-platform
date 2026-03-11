package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.vo.CalendarReminderVO;
import com.herb.platform.vo.DiseaseRecognitionVO;
import com.herb.platform.vo.HerbCalendarVO;
import com.herb.platform.vo.HerbDiseaseVO;
import com.herb.platform.vo.HerbKnowledgeVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Knowledge service.
 */
public interface KnowledgeService {

    IPage<HerbKnowledgeVO> pageKnowledge(String keyword, String herbCategory, String plantingSeason, String diseaseType, int pageNum, int pageSize);

    List<HerbDiseaseVO> listDiseases(String herbName, String diseaseType, String keyword);

    DiseaseRecognitionVO identifyDisease(String herbName, MultipartFile file);

    HerbCalendarVO getCalendar(Long userId, Long cropId, String herbName, LocalDate plantDate);

    List<CalendarReminderVO> getUpcomingReminders(Long userId, int days);
}
