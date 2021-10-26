package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.enums.ConclusionEnum;
import com.tanhua.common.enums.CoverEnum;
import com.tanhua.common.mapper.PaperMapper;
import com.tanhua.common.mapper.PaperOptionsMapper;
import com.tanhua.common.mapper.PaperQuestionsMapper;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.*;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.server.vo.DimensionsVo;
import com.tanhua.server.vo.ReportVo;
import com.tanhua.server.vo.SimilarYouVo;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TestSoulService {
    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private PaperQuestionsMapper paperQuestionsMapper;
    @Autowired
    private PaperOptionsMapper paperOptionsMapper;
    @Autowired
    private com.tanhua.common.mapper.ReportMapper reportMapper;

    @Autowired
    private com.tanhua.common.mapper.ReportMapper ReportMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Value("${tanhua.sso.default.recommend.users}")
    private String defaultRecommendUsers;
    @Autowired
    private UserMapper userMapper;


    public List<SoulPaper> getQuestion() {
        //查询问卷列表
        List<SoulPaper> papers = paperMapper.selectList(new QueryWrapper<>());
        for (SoulPaper paper : papers) {
            //获取试卷的id(1,2,3)
            String paperId = paper.getId();
            QueryWrapper<SoulQuestions> query = new QueryWrapper<>();
            query.eq("pid", paperId);
            //查询对应试卷的题目
            List<SoulQuestions> questions = paperQuestionsMapper.selectList(query);
            //将questions集合乱序
            Collections.shuffle(questions);
            List<SoulQuestions> newQuestions = new ArrayList<>();
            //将乱序后的集合倒序存入新的集合中
            for (int i = 1; i <= 10; i++) {
                if (questions.size() > 0) {
                    SoulQuestions questions1 = questions.remove(0);
                    newQuestions.add(questions1);
                }
            }
            //查询对应题目的选项
            if (newQuestions.size() > 0) {
                for (SoulQuestions questions1 : newQuestions) {
                    String qid = questions1.getId();
                    //获取问题id
                    QueryWrapper<SoulOptions> query1 = new QueryWrapper<>();
                    query1.eq("qid", qid);
                    //查询所有选项
                    List<SoulOptions> options = paperOptionsMapper.selectList(query1);
                    for (SoulOptions option : options) {
                        option.setOption(option.getOptions());
                    }
                    questions1.setOptions(options);
                }
            }
            paper.setQuestions(newQuestions);

            int i = 0;
            //试卷默认状态为锁定
            paper.setIsLock(1);
            if ("初级".equals(paper.getLevel())) {
                //灵魂测试默认将初级解锁不然无法解锁后续
                paper.setIsLock(0);
                i = 1;
            } else if ("中级".equals(paper.getLevel())) {
                //根据用户id和问卷id查询结果表 若结果表存在说明已经做过此问卷，并解锁下一等级的问卷
                QueryWrapper<SoulReport> query1 = new QueryWrapper<>();
                query1.eq("user_id", UserThreadLocal.get().getId());
                query1.eq("paper_id", "1");
                List<SoulReport> reports = reportMapper.selectList(query1);
                if (reports.size() > 0) {
                    paper.setIsLock(0);
                    i = 2;
                }
            } else if ("高级".equals(paper.getLevel())) {
                QueryWrapper<SoulReport> query2 = new QueryWrapper<>();
                query2.eq("user_id", UserThreadLocal.get().getId());
                query2.eq("paper_id", "2");
                List<SoulReport> reports = reportMapper.selectList(query2);
                if (reports.size() > 0) {
                    paper.setIsLock(0);
                    i = 3;
                }
            }
            //查询做过的试卷
            QueryWrapper<SoulReport> query3 = new QueryWrapper<>();
            query3.eq("user_id", UserThreadLocal.get().getId());
            query3.eq("paper_id", i);
            SoulReport soulReport = reportMapper.selectOne(query3);
            if (soulReport != null) {
                //如果该试卷做过获取到该试卷的报告id以将改试卷设置为查看报告而非开始测试
                paper.setReportId(soulReport.getId().toString());
            }
        }
        return papers;
    }


    public ResponseEntity submitPaper(AnswerList answers) {

        //定义报告对象，用以存入数据库
        SoulReport report = new SoulReport();
        User user = UserThreadLocal.get();
        report.setUserId(user.getId().toString());
        //获取前端传入的问题id和选项id集合
        List<Answers> answersList = answers.getAnswers();
        //设总分为0
        Integer total = 0;
        //遍历所有选项，获取到选项id
        int i = 0;
           /* for (Answers eachAnswer : answersList) {
                String optionId = eachAnswer.getOptionId();
                //获取到每一个现象id，并根据id查询到对应选项的分数
                QueryWrapper<SoulOptions> wraper=new QueryWrapper<>();
                wraper.eq("id",optionId);
                wraper.eq("qid",eachAnswer.getQuestionId());
                SoulOptions options = paperOptionsMapper.selectOne(wraper);
                //将所有分数相加
                i++;
                total+=options.getScore();
                System.out.println(i);
            }*/
        String pid = null;
        try {
            for (int i1 = 0; i1 < 10; i1++) {

                Answers answers1 = answersList.get(i);
                QueryWrapper<SoulOptions> wraper = new QueryWrapper<>();
                wraper.eq("id", answers1.getOptionId());
                wraper.eq("qid", answers1.getQuestionId());
                List<SoulOptions> options = paperOptionsMapper.selectList(wraper);
                //将所有分数相加
                i++;
                total += options.get(0).getScore();
                System.out.println(options);
                System.out.println(i);
            }
        } catch (Exception e) {
            System.out.println(total);
        }

        report.setScore(total);
        Integer reportType = null;
        //判断分数的范围，对应不同的报告类型，并写入数据库记录
        if (total <= 20) {
            reportType = 1;
        } else if (total <= 40) {
            reportType = 2;
        } else if (total <= 55) {
            reportType = 3;
        } else {
            reportType = 4;
        }
        report.setReportTypeId(reportType);
        //获取问题id，并根据问题的id判断其所属于某一张问卷

        String questionId = answers.getAnswers().get(0).getQuestionId();
        QueryWrapper<SoulReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        List<SoulReport> soulReports = reportMapper.selectList(queryWrapper);
        if(soulReports.size() == 1){
            questionId = "2" + questionId;
        }else if(soulReports.size() == 2){
            questionId = "1" + questionId;
        }
        QueryWrapper<SoulQuestions> query = new QueryWrapper<>();
        System.out.println("questionId = " + questionId);
        query.eq("cid", questionId);

        List<SoulQuestions> questions = paperQuestionsMapper.selectList(query);
        pid = questions.get(0).getPid();
        System.out.println("pid = " + pid);


        //将问卷表id写入报告表
        report.setPaperId(pid);
        QueryWrapper<SoulReport> queryIsExist = new QueryWrapper<>();
        queryIsExist.eq("user_id", user.getId().toString()).eq("paper_id", pid);
        //根据问卷id和用户id判断这个报告是否存在，即是否做过，存在就删除，然后新增，否则就直接新增，保证是最新一条报告
        reportMapper.delete(queryIsExist);

        reportMapper.insert(report);

        return ResponseEntity.ok(report.getId().toString());
    }


    public ResponseEntity ViewReport(String reportId) {
        ReportVo reportVo = null;
        try {
            // user = UserThreadLocal.get();
            //通过传递的报告id来查询对应的报告表
            SoulReport report = ReportMapper.selectById(reportId);
            //通过报告表查询出来的数据来获取用户对应的分数
            Integer score = report.getScore();
            //创建返回值对象
            reportVo = new ReportVo();
            //通过查询出来的报告类型来获取对应的枚举的数值
            reportVo.setConclusion(ConclusionEnum.getName(report.getReportTypeId()));
            reportVo.setCover(CoverEnum.getName(report.getReportTypeId()));
            //维度
            DimensionsVo dimensionsMap1 = new DimensionsVo();
            dimensionsMap1.setKey("外向");
            //通过随机数来获取对应的维度值
            dimensionsMap1.setValue(RandomUtils.nextInt(6, 10) + "0%");
            DimensionsVo dimensionsMap2 = new DimensionsVo();
            dimensionsMap2.setKey("判断");
            dimensionsMap2.setValue(RandomUtils.nextInt(6, 10) + "0%");
            DimensionsVo dimensionsMap3 = new DimensionsVo();
            dimensionsMap3.setKey("抽象");
            dimensionsMap3.setValue(RandomUtils.nextInt(6, 10) + "0%");
            DimensionsVo dimensionsMap4 = new DimensionsVo();
            dimensionsMap4.setKey("理性");
            dimensionsMap4.setValue(RandomUtils.nextInt(6, 10) + "0%");
            //创建一个返回值字段的集合
            List<DimensionsVo> dimensionsVos = new ArrayList<>();
            //将维度添加至集合
            dimensionsVos.add(dimensionsMap1);
            dimensionsVos.add(dimensionsMap2);
            dimensionsVos.add(dimensionsMap3);
            dimensionsVos.add(dimensionsMap4);
            //设置参数
            reportVo.setDimensions(dimensionsVos);
            //相似
            String userId = report.getUserId();
            //获取对应的用户id
            List<SoulReport> reports = ReportMapper.selectList(new QueryWrapper<SoulReport>().eq("score", score));
            //创建一个查询条件的集合
            List<Long> userIdList = new ArrayList<>();
            //判断查询出来的相似的人的个数是否大于10个
            if (reports.size() >= 10) {
                //循环获取查询的相似用户的id
                for (SoulReport userIds : reports) {
                    //判断集合中是否有当前用户id
                    if (!userIdList.contains(userIds.getUserId())) {
                        //添加到集合中
                        userIdList.add(new Long(userIds.getUserId()).longValue());
                    }
                }
            } else {
                //默认相似用户的id列表
                String[] split = defaultRecommendUsers.split(",");
                // //循环获取查询的相似用户的id
                for (SoulReport userIds : reports) {
                    // //判断集合中是否有当前用户id
                    if (!userIdList.contains(userIds.getUserId())) {
                        //不是的添加到集合中
                        userIdList.add(new Long(userIds.getUserId()).longValue());
                    }
                }
                //循环默认的用户id
                for (String s : split) {
                    //添加到集合中
                    userIdList.add(new Long(s).longValue());
                }
            }
            //查询对应用户id的账号信息
            List<UserInfo> userInfos = userInfoService.queryUserInfoList(new QueryWrapper<UserInfo>().in("user_id", userIdList));
            //创建返回对象的参数
            List<SimilarYouVo> similarYouVos = new ArrayList<>();
            //循环出查询出来的用户信息集合
            for (UserInfo userInfo : userInfos) {
                SimilarYouVo similarYouVo = new SimilarYouVo();
                similarYouVo.setId(new Long(userInfo.getId()).intValue());
                similarYouVo.setAvatar(userInfo.getLogo());
                similarYouVos.add(similarYouVo);
            }
            //返回对象
            reportVo.setSimilarYou(similarYouVos);
            return ResponseEntity.ok(reportVo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}




