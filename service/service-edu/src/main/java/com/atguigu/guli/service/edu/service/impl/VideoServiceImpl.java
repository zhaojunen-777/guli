package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.form.VideoInfoForm;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author en
 * @since 2020-02-13
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Override
    public void saveVideoInfo(VideoInfoForm videoInfoForm) {
        Video video = new Video();
        BeanUtils.copyProperties(videoInfoForm,video);
        baseMapper.insert(video);
    }

    @Override
    public VideoInfoForm getVideoInfoFormById(String id) {
        Video video = this.getById(id);
        //创建videoInfoForm对象
        VideoInfoForm videoInfoForm = new VideoInfoForm();
        BeanUtils.copyProperties(video, videoInfoForm);

        return videoInfoForm;
    }

    @Override
    public void updateVideoInfoById(VideoInfoForm videoInfoForm) {
        Video video = new Video();
        BeanUtils.copyProperties(videoInfoForm, video);
        baseMapper.updateById(video);
    }

    @Override
    public void removeVideoById(String id) {
        baseMapper.deleteById(id);
    }


}
