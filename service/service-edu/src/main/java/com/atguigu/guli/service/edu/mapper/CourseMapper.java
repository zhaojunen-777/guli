package com.atguigu.guli.service.edu.mapper;

import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author en
 * @since 2020-02-13
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    CoursePublishVo selectCoursePublishVoById(String id);

    WebCourseVo selectWebCourseVoById(String courseId);
}
