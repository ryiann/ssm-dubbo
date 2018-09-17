package com.ryan.controller;

import com.ryan.pojo.ResponseEntity;
import com.ryan.service.StudentDubboService;
import com.ryan.utils.BaseController;
import com.ryan.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YoriChen
 * @date 2018/5/21
 */
@Controller
public class StudentController extends BaseController {

    @Autowired
    StudentDubboService studentDubboService;

    @ResponseBody
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/json/findStudentListByPage")
    public void findStudentListByPage(StudentVO stu){
        ResponseEntity<List<StudentVO>> stuList = studentDubboService.findStudentListByPage(stu);
        writeJson(stuList);
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/json/findStudentInfoByStuId")
    public void findStudentInfoByStuId(Integer stuId){
        ResponseEntity<StudentVO> stuList = studentDubboService.findStudentInfoByStuId(stuId);
        writeJson(stuList);
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/json/insertStudent")
    public void insertStudent(StudentVO stu){
        ResponseEntity<Object> resObj = studentDubboService.insertStudent(stu);
        writeJson(resObj);
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/json/updateStudentByStuId")
    public void updateStudentByStuId(StudentVO stu){
        ResponseEntity<Object> resObj = studentDubboService.updateStudentByStuId(stu);
        writeJson(resObj);
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/json/deleteStudentByStuId")
    public void deleteStudentByStuId(Integer stuId){
        ResponseEntity<Object> resObj = studentDubboService.deleteStudentByStuId(stuId);
        writeJson(resObj);
    }
}