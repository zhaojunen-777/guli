package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.util.ExcelImportUtil;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.atguigu.guli.service.edu.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.velocity.runtime.directive.Foreach;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author en
 * @since 2020-02-13
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchImport(InputStream inputStream) throws Exception {
        ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(inputStream);
        HSSFSheet sheet = excelHSSFUtil.getSheet();
        for (Row row : sheet){
            if (row.getRowNum() == 0){
                continue;
            }
            Cell levelOneCell = row.getCell(0);
            String levelOneValue = excelHSSFUtil.getCellValue(levelOneCell).trim();
            if (levelOneCell == null || StringUtils.isEmpty(levelOneValue)){
                continue;
            }
            Cell levelTwoCell = row.getCell(1);
            String levelTwoValue = excelHSSFUtil.getCellValue(levelTwoCell).trim();
            if (levelTwoValue == null || StringUtils.isEmpty(levelTwoValue)){
                continue;
            }
            Subject subject = getByTitle(levelOneValue);
            String parentId = null;
            if (subject == null){
                Subject subjectLevelOne = new Subject();
                subjectLevelOne.setTitle(levelOneValue);
                baseMapper.insert(subjectLevelOne);
                parentId = subjectLevelOne.getId();
            }else {
                parentId = subject.getId();
            }
            Subject subjectSub = getSubByTitle(levelTwoValue, parentId);
            Subject subjectLevelTwo = null;
            if (subjectSub == null){
                subjectLevelTwo = new Subject();
                subjectLevelTwo.setTitle(levelTwoValue);
                subjectLevelTwo.setParentId(parentId);
                baseMapper.insert(subjectLevelTwo);
            }
        }
    }

    @Override
    public List<SubjectVo> nestedList() {
        List<SubjectVo> subjectVoList = new ArrayList<>();
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sort","id");
        List<Subject> subjectList = baseMapper.selectList(queryWrapper);
        List<Subject> subjectOneLevelList = new ArrayList<>();
        List<Subject> subjectTwoLevelList = new ArrayList<>();
        for (Subject subject : subjectList) {
            if (subject.getParentId().equals("0")){
                subjectOneLevelList.add(subject);
            }else {
                subjectTwoLevelList.add(subject);
            }
        }
        for (Subject subject : subjectOneLevelList) {
            SubjectVo subjectVoOneLevel = new SubjectVo();
            BeanUtils.copyProperties(subject,subjectVoOneLevel);
            subjectVoList.add(subjectVoOneLevel);
            
            List<SubjectVo> subjectVoTwoLevelList = new ArrayList<>();
            for (Subject subject1 : subjectTwoLevelList) {
                if (subject.getId().equals(subject1.getParentId())){
                    SubjectVo subjectVoTwoLevel = new SubjectVo();
                    BeanUtils.copyProperties(subject1,subjectVoTwoLevel);
                    subjectVoTwoLevelList.add(subjectVoTwoLevel);
                }
            }
            subjectVoOneLevel.setChildren(subjectVoTwoLevelList);
        }

        return subjectVoList;
    }

    @Override
    public List<SubjectVo> nestedList2() {
        List<SubjectVo> subjectVoList = baseMapper.selectNestedListByParentId("0");
        return subjectVoList;
    }

    private Subject getByTitle(String title){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id","0");
        return baseMapper.selectOne(queryWrapper);
    }
    private Subject getSubByTitle(String title,String parentId){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
