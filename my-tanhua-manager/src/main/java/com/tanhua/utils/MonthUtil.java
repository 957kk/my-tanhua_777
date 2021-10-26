package com.tanhua.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
 
 
public class MonthUtil {
	/*
	 * 获取两个时间之间的月
	 * 时间格式 yyyy-MM  或者yyyy-MM-dd
	 */
	public static  List<String> getAllMonths(String start, String end){
		List<String> list=new ArrayList<String>();
        String splitSign="-";
        start=start.substring(0,7);
        end=end.substring(0,7);
        String regex="\\d{4}"+splitSign+"(([0][1-9])|([1][012]))"; //判断YYYY-MM时间格式的正则表达式
        if(!start.matches(regex) || !end.matches(regex)) {
            return list;
        }
        if(start.compareTo(end)>0){
            //start大于end日期时，互换
            String temp=start;
            start=end;
            end=temp;
        }
        String temp=start; //从最小月份开始
        while(temp.compareTo(start)>=0 && temp.compareTo(end)<=0){
            list.add(temp); //首先加上最小月份,接着计算下一个月份
            String[] arr=temp.split(splitSign);
            int year=Integer.valueOf(arr[0]);
            int month=Integer.valueOf(arr[1])+1;
            if(month>12){
                month=1;
                year++;
            }
            if(month<10){//补0操作
                temp=year+splitSign+"0"+month;
            }else{
                temp=year+splitSign+month;
            }
        }
        return list;
    }
	/*
	 * 获取两个时间之间的天
	 * 时间格式 yyyy-MM-dd
	 */
	public static List<String> getAllDays(String start, String ends){
		 List<String> result = new ArrayList<String>();
         Calendar tempStart = Calendar.getInstance();
         SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
         Date begin =null ;Date end = null;
		try {
			begin = fmt.parse(start);
			end=fmt.parse(ends);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return result;
		}
         tempStart.setTime(begin);
      while(begin.getTime()<=end.getTime()){
          result.add(fmt.format(tempStart.getTime()));
          tempStart.add(Calendar.DAY_OF_YEAR, 1);
          begin = tempStart.getTime();
      }
         return result;
		
	}
	/*
	 * 获取月的天
	 * 时间格式 yyyy-MM-dd
	 */
	public static List<String> getMonthDay(String year,String month){
		 List<String> result = new ArrayList<String>();
         Calendar tempStart = Calendar.getInstance();
         SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
         Date begin =null ;Date end = null;
		try {
			if(null==year||""==year||null==month||""==month){
				return result;
			}
			begin = fmt.parse(year+"-"+month+"-"+"01");
			end=fmt.parse(year+"-"+(Integer.valueOf(month)+1)+"-"+"01");;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
         tempStart.setTime(begin);
      while(begin.getTime()<end.getTime()){
          result.add(fmt.format(tempStart.getTime()));
          tempStart.add(Calendar.DAY_OF_YEAR, 1);
          begin = tempStart.getTime();
      }
         return result;
		
	}
	/*
	 * 获取两个时间之间的天数
	 * 时间格式 yyyy-MM-dd
	 */
	public static Integer getDaySum(String start, String ends){
		Integer result = 0;
         Calendar tempStart = Calendar.getInstance();
         SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
         Date begin =null ;Date end = null;
		try {
			begin = fmt.parse(start);
			end=fmt.parse(ends);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
         tempStart.setTime(begin);
      while(begin.getTime()<end.getTime()){
          result+=1;
          tempStart.add(Calendar.DAY_OF_YEAR, 1);
          begin = tempStart.getTime();
      }
         return result;
		
	}
//    public static void main(String[] args) {
//        String start="2010-7-01";
//        String end="2010-7-01";
//        Integer aInteger=MonthUtil.getDaySum(start, end);
//        System.out.println(aInteger);
//        List<String> list=	MonthUtil.getAllDays(start, end);
//        String[] result=MonthUtil.getAllMonths(start, end);
//        for (String str : result) {
//            System.out.println(str);
//        }
//        System.out.println(list.toString());
        
//    }
}