package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author en
 * @since 2020-02-13
 */
public interface CourseService extends IService<Course> {

    String saveCourseIofo(CourseInfoForm courseInfoForm);

    CourseInfoForm getCourseIofoById(String id);

    void updateCourseInfoById(CourseInfoForm courseInfoForm);

    void selectPage(Page<Course> pageParam, CourseQueryVo courseQueryVo);


    void removeCourseById(String id);

    CoursePublishVo getCoursePublishVoById(String id);

    void publishCourseById(String id);

    Map<String,Object> webSelectPage(Page<Course> pageParam, WebCourseQueryVo webCourseQueryVo);

    WebCourseVo selectWebCourseVoById(String id);

    CourseDto getCourseDtoById(String courseId);
}
