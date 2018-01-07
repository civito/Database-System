import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Scanner;

public class University {
	public static void main(String[] argv) {         
        Scanner sc = new Scanner(System.in);
        int select = 0;
        int user = -1;
        //int period=1;
        String studentId = null;
        
        // Connect to the database
        try {
            Connection con = getDBConn();
            System.out.println("수강신청 로그인");
            System.out.print("학번 : ");
            studentId = sc.nextLine();
            
            while(true) {
            	if(studentId.isEmpty()){
            		System.out.println("학번을 제대로 입력해주세요");
            		System.out.print("학번 : ");
            		studentId = sc.nextLine();
            		}
            	else if(studentId.equals("root")){//admin 로그인
            		System.out.print("Password : ");
            		String password = sc.nextLine();
            		
            		while(!password.equals("root")) {
            			System.out.println("비밀번호가 틀렸습니다.");
            			System.out.print("Password : ");
            			password = sc.nextLine();
            			}
            		user = 2;
            		break;
            		}
            	else if(!checkStudents(con, studentId)){
            		System.out.println("존재하지 않는 학번입니다");
            		System.out.print("학번 : ");
            		studentId = sc.nextLine();
            		}
            	else{
            		System.out.print("Password : ");
            		String password = sc.nextLine();
            		
            		while(!checkPassword(con, studentId, password)) {
            			System.out.println("비밀번호가 틀렸습니다.");
            			System.out.print("Password : ");
            			password = sc.nextLine();
            		
            			}
            		
            		user = 1;
            		break;
                	}
               }
            
            if(user == 1){ //학생으로 로그인
            	Statement stmt = con.createStatement();
            	ResultSet rset = stmt.executeQuery("SELECT student.name, major.name 
				                                    FROM Student, Major 
													WHERE student.major_id=major.major_id and student_id = '"+studentId+"';");
            	if(rset.next()){
            		System.out.println(rset.getString(1)+" "+rset.getString(2)+" 수강신청 접속 완료");
            	}
               	stmt.close();
            	
            	while(true) {
                    System.out.println("====================");
                    System.out.println("1.수강 신청");
                    System.out.println("2.수강 취소");
                    System.out.println("3.수강 편람");
                    System.out.println("4.수강 내역 확인");
                    System.out.println("5.시간표 확인");
                    System.out.println("6.종료");
                    System.out.println("====================");
                    System.out.print("실행할 동작을 선택하세요. : ");
                    select = sc.nextInt();
                    sc.nextLine();
                    
                    if(select == 1){  // 수강신청

    	                System.out.println("======수강신청======");
                    	String tmp;
                    	while(true) {
    		            	
                    		System.out.println("**최대 20학점까지 수강신청 가능합니다.**");
                    		System.out.print("신청할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                    		int classId = sc.nextInt();
                    		sc.skip("\n");
                    		
                            while(true) {
                            	if(!checkCourse(con, classId)){
                            		System.out.println("존재하지 않는 강의 번호입니다.");
                            		System.out.print("신청할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                            		classId = sc.nextInt();
                            		sc.skip("\n");
                            	}
                            	else if(checkTakes(con, studentId, classId)){
                            		System.out.println("이미 수강하고 있는 과목입니다.");
                            		System.out.print("신청할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                            		classId = sc.nextInt();
                            		sc.skip("\n");
                            	}
                            	
                            	else{
                            		int timeId=showTime(con, classId);
                            		if(checkTime(con, studentId, classId, timeId)){
                            			System.out.println("다른 과목과 시간이 겹칩니다.");
                                		System.out.print("신청할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                                		classId = sc.nextInt();
                                		sc.skip("\n");
                            		}
                            		else{
                            			insertTakes(con, studentId, classId, timeId);
                            			break;
                            		}
                            	}
                            }
                             				        
    				        do{
					        	System.out.print("수강신청을 계속?(y/n) : ");
					        	tmp = sc.nextLine();
					        
					        }while(!(tmp.equals("y") || tmp.equals("Y") || tmp.equals("n") || tmp.equals("N")));
					        
					        if(tmp.equals("n") || tmp.equals("N"))
					        	break;
    				    }
                    	updateCurrentCredits(con, studentId);
            		}
            
                    else if(select == 2){  // 수강취소
    	                System.out.println("=====수강취소=====");
                    	String tmp;
                    	while(true) {
                    		//displayMyCourse(con, studentId);
                    		
                    		System.out.print("취소할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                    		int classId = sc.nextInt();
                    		sc.skip("\n");
                            while(true) {
                            	if(!checkCourse(con, classId)){
                            		System.out.println("존재하지 않는 강의 번호입니다");
                            		System.out.print("select courseId : ");
                            		classId = sc.nextInt();
                            		sc.skip("\n");
                            	}
                            	else if(!checkTakes(con, studentId, classId)){
                            		System.out.println("수강하고 있지 않은 과목입니다");
                            		System.out.print("취소할 과목의 강의 번호를 입력하세요.(예-8831) : ");
                            		classId = sc.nextInt();
                            		sc.skip("\n");
                            	}
                            	else{
                            		deleteTakes(con, studentId, classId);
        					        System.out.println("취소 완료되었습니다\n");
        					        break;
                            	}
                            }
                            
                            do{
    				        	System.out.print("수강취소 계속?(y/n) : ");
    				        	tmp = sc.nextLine();
    				        
    				        }while(!(tmp.equals("y") || tmp.equals("Y") || tmp.equals("n") || tmp.equals("N")));
    				        
    				        if(tmp.equals("n") || tmp.equals("N"))
    				        	break;
    				    }                    	
                    	updateCurrentCredits(con, studentId);
                    }
            		else if(select == 3){//수강편람
    	                System.out.println("======수강 편람=======");
    	                System.out.println("1.수업 번호");
    	                System.out.println("2.학수 번호");
    	                System.out.println("3.교과목명");
    	                System.out.println("4.교강사명");
    	                System.out.println("====================");
    	                System.out.print("실행할 동작을 선택하세요. : ");
    	                int select_2 = sc.nextInt();
    	                
    	                if(select_2 == 1){//수업 번호 
    	                	System.out.print("편람하고자하는 연도를 입력해주세요.(2011~2014) : ");
    	                	int select_year=sc.nextInt();
    	                	System.out.print("수업 번호 (예-10000) : ");
    	                	int class_no = sc.nextInt();
    	                	displayCourse_classno(con, class_no, select_year);
    	                }
    	                else if(select_2 == 2){//학수 번호 
    	                	System.out.print("편람하고자하는 연도를 입력해주세요.(2011~2014) : ");
    	                	int select_year=sc.nextInt();
    	                	System.out.print("학수 번호 (예-CSE0000) : ");
    	                	String course_id = sc.next();
    	                	displayCourse_courseid(con, course_id, select_year);
    	                }
    	                else if(select_2 == 3){//교과목명 
    	                	System.out.print("편람하고자하는 연도를 입력해주세요.(2011~2014) : ");
    	                	int select_year=sc.nextInt();
    	                	System.out.print("교과목명 : ");
    	                	String course_id = sc.next();
    	                	displayCourse_name(con, course_id, select_year);
    	                }
    	                else{//교강사명
    	                	System.out.print("편람하고자하는 연도를 입력해주세요.(2011~2014) : ");
    	                	int select_year=sc.nextInt();
    	                	System.out.print("교강사명 : ");
    	                	String course_id = sc.next();
    	                	displayCourse_lecturer(con, course_id, select_year);
    	                }
                    }
            		else if(select == 4){
            			displayMyCourse(con, studentId);
            		}
            		else if(select==5){
            			timeTable(con, studentId);
            		}
                    else if(select == 6){
                    	System.out.println("수강신청 프로그램을 종료합니다.");
                    	break;
                    }
                    else{
                    	System.out.println("please insert number 1~6");
                    }
            	}
            }
            else if(user==2){//관리자로 로그인
            	System.out.println("관리자로 접속하였습니다.");
            	while(true) { 
            		System.out.println("====================");
                    System.out.println("1.설강");
	                System.out.println("2.폐강");
	                System.out.println("3.통계");
	                System.out.println("4.종료");
	                System.out.println("====================");
                    System.out.print("실행할 동작을 선택하세요. : ");

	                int select2 = sc.nextInt();
	                sc.nextLine();
	                    
	                if(select2 == 1) {
	                	System.out.println("=====강의 등록=====");
	                	
	                	System.out.print("학수 번호를 입력하세요(예-CSE3022) : ");
		            	String courseId = sc.nextLine();
		            	courseId=courseId.toUpperCase();
		            	
		            	while(!checkCourseId(con, courseId)) {
		            		System.out.println("학수번호가 존재하지 않습니다. ");
		            		System.out.print("학수 번호를 입력하세요(예-CSE3022) : ");
		            		courseId = sc.nextLine();
		            		courseId=courseId.toUpperCase();
		            	}
		            	
		            	System.out.print("강의 번호를 입력하세요(예-8831/4자리) : ");
		            	int classId = sc.nextInt();
		            	sc.skip("\n");
		          
		            	while(checkCourse(con, classId)) {
		            		System.out.println("강의번호가 중복 됩니다 ");
		            		System.out.print("강의 번호를 입력하세요(예-8831/4자리) : ");
		            		classId = sc.nextInt();
			            	sc.skip("\n");
		            	}
		            	System.out.print("전공 번호를 입력하세요.(예-40) : ");
		            	int majorId = sc.nextInt();
		            	sc.skip("\n");
		            	while(!checkMajor(con, majorId)){
		            		System.out.println("전공 번호가 존재하지 않습니다.");
		            		System.out.print("전공 번호를 입력하세요.(예-40) : ");
		            		majorId = sc.nextInt();
			            	sc.skip("\n");
		            	}
			            System.out.print("교강사 번호를 입력하세요(예-2001001001) : ");
		            	String lecturerId = sc.nextLine();
		            	while(!checkLecturerId(con, lecturerId)) {
		            		System.out.println("교강사 번호가 존재하지 않습니다.");
		            		System.out.print("교강 번호를 입력하세요(예-2001001001) : ");
		            		lecturerId = sc.nextLine();
			            	}
		            	
		            	System.out.print("최대 인원 입력하세요 : ");
		            	int personMax = sc.nextInt();
		            	sc.skip("\n");
		            	
		            	System.out.print("강의실 번호를 입력하세요(1-199/강의실이 없다면 0) : ");
		            	int	roomId = sc.nextInt();
		            	sc.skip("\n");
		            	while(!checkRoomId(con, roomId)) {
		            		System.out.println("강의실 번호가 존재하지 않습니다.");
		            		System.out.print("강의실 번호를 입력하세요(1-199/강의실이 없다면 0) : ");
		            		roomId = sc.nextInt();
		            		sc.skip("\n");
			            	}
		            	System.out.print("교시를 입력하세요.(예-1) : ");
		            	int period = sc.nextInt();
		            	sc.skip("\n");
		            	while(checkPeriod(con, classId, period)){
		            		System.out.println("동일한 과목에 동일한 교시가 존재합니다.");
		            		System.out.print("교시를 입력하세요.(예-1) : ");
			            	period = sc.nextInt();
			            	sc.skip("\n");
		            	}
		            	
		            	System.out.print("강의 시작시간을 입력하세요.(예-월05:30) : ");
		            	String begin = sc.nextLine();
			           
		            	System.out.print("강의 종료시간을 입력하세요.(예-월07:00) : ");
		            	String end = sc.nextLine();
		            	while(checkSameTime(con, begin, end, roomId)){
		            		System.out.println("해당 시간에 강의실 "+roomId+"을 이미 사용 중인 강의가 있습니다.");
		            		System.out.print("강의 시작시간을 입력하세요.(예-월05:30) : ");
		            		begin = sc.nextLine();
				           
		            		System.out.print("강의 종료시간을 입력하세요.(예-월07:00) : ");
		            		end = sc.nextLine();
		            	}
		            	insertToClass(con, classId,  courseId, majorId, lecturerId, personMax, roomId);
		            	insertToTime(con, classId, period, begin, end);
	                	
	                }
	                else if(select2 == 2){
	                    
	    	            System.out.println("=====강의 삭제=====");
	    		        System.out.print("삭제할 강의의 강의 번호를 입력해주세요.(예-8831) : ");
	    		        int classId = sc.nextInt();
	    		        sc.skip("\n");
	    		        while(!checkCourse(con, classId)) {
	    		            System.out.println("존재하지 않는 강의 번호입니다.");
	    		            System.out.print("삭제할 강의의 강의 번호를 입력해주세요.(예-8831) : ");
	    		            classId = sc.nextInt();
	    		            sc.skip("\n");
	    		        }
	    		        int period=showTime2(con, classId);
	    		        deleteFromTime(con, classId, period);
	    		        deleteFromCourseenrolled(con, classId);
	    		        deleteFromClass(con, classId);
	    	   		    
	                }
	                else if(select2==3){
	                	System.out.print("강의 통계를 보고자 하는 전공의 번호를 입력해주세요. : ");
	                	int majorId = sc.nextInt();
	                	sc.skip("\n");
	                	while(!checkMajor(con, majorId)) {
	    		            System.out.println("존재하지 않는 전공 번호입니다.");
	    		            System.out.print("강의 통계를 보고자 하는 전공의 번호를 입력해주세요. : ");
	    		            majorId = sc.nextInt();
	    		            sc.skip("\n");
	    		        }
	                	showMajorStatistic(con, majorId);
	                }
	                else if(select2==4){
	                	System.out.println("수강신청 프로그램을 종료합니다.");
                    	break;
                    }
                    else{
                    	System.out.println("please insert number 1~6");
                    }
            	}
            }
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
}
    
	public static Connection getDBConn() {
		String url="jdbc:mysql://localhost:3306/university";
		String user="root";
		String password="1234";
		Connection DBConn = null;
				
		try {
			DBConn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return DBConn;
	}
	public static boolean checkStudents(Connection con, String studentId) throws SQLException {
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT student_id 
		                                    FROM Student 
											WHERE student_id = '" + studentId +"';");
    	boolean result = false;
    	
    	
    	if(rset.next()) {
    		result = true;
        }

        stmt.close();
        return result;
    }
	public static boolean checkPassword(Connection con, String studentId, String password) throws SQLException {
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT password 
		                                    FROM Student  
										    WHERE student_id = '" + studentId +"';");
    	boolean result = false;
    	
    	if(rset.next()) {
    		if(rset.getString(1).equals(password)){
    			result = true;
    		}
        }

        stmt.close();
        return result;
    }
	public static void displayCourse_classno(Connection con, int class_no, int select_year) throws SQLException {
    	
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	int i=1;
    	//JOIN연산
    	ResultSet rset = stmt.executeQuery("SELECT class.class_id, class.class_no, class.course_id, class.name, class.year, class.credit, lecturer.name, class.room_id
		                                    FROM Class, Lecturer 
											WHERE Class.lecturer_id = Lecturer.lecturer_id and Class.opened = '"+select_year+"' and class_no='"+class_no+"';");
    	if(rset.next()){
    		System.out.print("*********************\n");
			System.out.print("강의 번호 : ");
			System.out.println(rset.getString(1));
			int classId = rset.getInt(1);
			System.out.print("수업 번호 : ");
			System.out.println(rset.getString(2));
			System.out.print("학수 번호 : ");
			System.out.println(rset.getString(3));
			System.out.print("교과목명 : ");
			System.out.println(rset.getString(4));
			System.out.print("학년 : ");
			System.out.println(rset.getString(5));
			System.out.print("학점 : ");
			System.out.println(rset.getString(6));
			System.out.print("교강사명  : ");
			System.out.println(rset.getString(7));
			System.out.print("강의실 번호  : ");
			System.out.println(rset.getString(8));
			if(select_year==2014){
				ResultSet rset2 = stmt2.executeQuery("SELECT time.period, time.begin, time.end FROM Class, time WHERE time.class_id = class.class_id and time.class_id='"+classId+"';");
				while(rset2.next()){
				
					System.out.println(rset2.getInt(1)+"."+"시작 시간 : "+rset2.getString(2)+" 종료 시간 : "+rset2.getString(3));
					i+=1;
				}
				System.out.print("*********************\n");
			}
    		
			else
				System.out.print("*********************\n");
    	} else {
        	System.out.println("과목이 존재하지 않습니다");    		
    	}
    	stmt.close();
    	stmt2.close();
    }
	public static void displayCourse_courseid(Connection con, String course_id, int select_year) throws SQLException {
    	
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	//JOIN연산
    	ResultSet rset = stmt.executeQuery("SELECT class.class_id, class.class_no, class.course_id, class.name, class.year, class.credit, lecturer.name, class.room_id 
		                                    FROM Class, Lecturer 
											WHERE Class.lecturer_id = Lecturer.lecturer_id and Class.opened = '"+select_year+"' and course_id ='"+course_id+"';");
    	int i=1;
    	
    	if(rset.next()){
    		do{
    		System.out.print("*********************\n");
			System.out.print("강의 번호 : ");
			System.out.println(rset.getString(1));
			int classId = rset.getInt(1);
			System.out.print("수업 번호 : ");
			System.out.println(rset.getString(2));
			System.out.print("학수 번호 : ");
			System.out.println(rset.getString(3));
			System.out.print("교과목명 : ");
			System.out.println(rset.getString(4));
			System.out.print("학년 : ");
			System.out.println(rset.getString(5));
			System.out.print("학점 : ");
			System.out.println(rset.getString(6));
			System.out.print("교강사명  : ");
			System.out.println(rset.getString(7));
			System.out.print("강의실 번호  : ");
			System.out.println(rset.getString(8));
			if(select_year==2014){
				ResultSet rset2 = stmt2.executeQuery("SELECT time.period, time.begin, time.end FROM Class, time WHERE time.class_id = class.class_id and time.class_id='"+classId+"';");
				while(rset2.next()){
				
					System.out.println(rset2.getInt(1)+"."+"시작 시간 : "+rset2.getString(2)+" 종료 시간 : "+rset2.getString(3));
					i+=1;
				}
				System.out.print("*********************\n");
			}
    		
			else
				System.out.print("*********************\n");
    		}while(rset.next());
    	} 
    	else {
        	System.out.println("과목이 존재하지 않습니다");    		
    	}
    	stmt.close();    
    	stmt2.close();
    }
	public static void displayCourse_name(Connection con, String name, int select_year) throws SQLException {
    	
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	int i=1;
    	//JOIN연산, 키워드 검색 
    	ResultSet rset = stmt.executeQuery("SELECT class.class_id, class.class_no, class.course_id, class.name, class.year, class.credit, lecturer.name, class.room_id 
		                                    FROM Class, Lecturer 
											WHERE Class.lecturer_id = Lecturer.lecturer_id and Class.opened = '"+select_year+"' and class.name like '%"+name+"%';");
    	if(rset.next()){
    		do{
    			System.out.print("*********************\n");
    			System.out.print("강의 번호 : ");
    			System.out.println(rset.getString(1));
    			int classId = rset.getInt(1);
    			System.out.print("수업 번호 : ");
    			System.out.println(rset.getString(2));
    			System.out.print("학수 번호 : ");
    			System.out.println(rset.getString(3));
    			System.out.print("교과목명 : ");
    			System.out.println(rset.getString(4));
    			System.out.print("학년 : ");
    			System.out.println(rset.getString(5));
    			System.out.print("학점 : ");
    			System.out.println(rset.getString(6));
    			System.out.print("교강사명  : ");
    			System.out.println(rset.getString(7));
    			System.out.print("강의실 번호  : ");
    			System.out.println(rset.getString(8));
    			if(select_year==2014){
    				ResultSet rset2 = stmt2.executeQuery("SELECT time.period, time.begin, time.end FROM Class, time WHERE time.class_id = class.class_id and time.class_id='"+classId+"';");
    				while(rset2.next()){
    				
    					System.out.println(rset2.getInt(1)+"."+"시작 시간 : "+rset2.getString(2)+" 종료 시간 : "+rset2.getString(3));
    					i+=1;
    				}
    				System.out.print("*********************\n");
    			}
        		
    			else
    				System.out.print("*********************\n");
    		}while(rset.next());
    	} else {
        	System.out.println("과목이 존재하지 않습니다");    		
    	}
    	stmt.close();
    	stmt2.close();
    }
	public static void displayCourse_lecturer(Connection con, String name, int select_year) throws SQLException {
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	int i=1;
    	//JOIN연산, 전방일치 검색 
    	ResultSet rset = stmt.executeQuery("SELECT class.class_id, class.class_no, class.course_id, class.name, class.year, class.credit, lecturer.name, class.room_id FROM Class, Lecturer WHERE Class.lecturer_id = Lecturer.lecturer_id and Class.opened = '"+select_year+"' and lecturer.name like '"+name+"%';");
    	//ResultSet rset = stmt.executeQuery("SELECT class.class_no, class.course_id, class.name, class.year, class.credit, lecturer.name FROM Class JOIN Lecturer ON Class.lecturer_id = Lecturer.lecturer_id WHERE class_no='"+class_no+"';");
    	if(rset.next()){
    		do{
    			System.out.print("*********************\n");
    			System.out.print("강의 번호 : ");
    			System.out.println(rset.getString(1));
    			int classId = rset.getInt(1);
    			System.out.print("수업 번호 : ");
    			System.out.println(rset.getString(2));
    			System.out.print("학수 번호 : ");
    			System.out.println(rset.getString(3));
    			System.out.print("교과목명 : ");
    			System.out.println(rset.getString(4));
    			System.out.print("학년 : ");
    			System.out.println(rset.getString(5));
    			System.out.print("학점 : ");
    			System.out.println(rset.getString(6));
    			System.out.print("교강사명  : ");
    			System.out.println(rset.getString(7));
    			System.out.print("강의실 번호  : ");
    			System.out.println(rset.getString(8));
    			if(select_year==2014){
    				ResultSet rset2 = stmt2.executeQuery("SELECT time.period, time.begin, time.end FROM Class, time WHERE time.class_id = class.class_id and time.class_id='"+classId+"';");
    				while(rset2.next()){
    				
    					System.out.println(rset2.getInt(1)+"."+"시작 시간 : "+rset2.getString(2)+" 종료 시간 : "+rset2.getString(3));
    					i+=1;
    				}
    				System.out.print("*********************\n");
    			}
        		
    			else
    				System.out.print("*********************\n");
    		}while(rset.next());
    	} else {
        	System.out.println("과목이 존재하지 않습니다");    		
    	}
    	stmt.close();
    	stmt2.close();
    }
	public static void displayMyCourse(Connection con, String studentId) throws SQLException{
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT class.name, class.course_id, class.credit, class.room_id, lecturer.name, student.current_credits, time.begin, time.end, class.class_id FROM class, lecturer, courseenrolled, student, time WHERE courseenrolled.student_id=student.student_id and courseenrolled.class_id=class.class_id and courseenrolled.time_id=time.time_id and class.lecturer_id=lecturer.lecturer_id and class.opened='2014' and student.student_id='"+studentId+"';");
		if(rset.next()){
			System.out.print("*********************\n");
			System.out.println("현재 신청 학점 : "+rset.getInt(6));
			do{
				System.out.print("*********************\n");
    			System.out.print("교과목명 : ");
    			System.out.println(rset.getString(1));
    			System.out.print("강의 번호 : ");
    			System.out.println(rset.getString(9));
    			System.out.print("학수 번호 : ");
    			System.out.println(rset.getString(2));
    			System.out.print("학점 : ");
    			System.out.println(rset.getString(3));
    			System.out.print("강의실 번호 : ");
    			System.out.println(rset.getString(4));
    			System.out.print("교강사명 : ");
    			System.out.println(rset.getString(5));
    			System.out.println("시작 : "+rset.getString(7)+" 종료 : "+rset.getString(8));
    			System.out.print("*********************\n");
    			} while(rset.next());
		}
		else {
        	System.out.println("수강신청 내역이 없습니다.");    		
    	}
    	stmt.close();
	}
	public static boolean checkPeriod(Connection con, int classId, int period) throws SQLException{
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT period FROM time WHERE class_id = '"+classId+"';");
		boolean result = false;
		while(rset.next()){
			if(rset.getInt(1)==period){
				result=true;
				break;
			}
		}
		stmt.close();
		return result;
	}
	public static boolean checkCourse(Connection con, int classId) throws SQLException {//강의 존재 여부 확인
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT class_id FROM class WHERE opened='2014' and class_id = '" + classId +"';");
    	boolean result = false;
    	
    	if(rset.next()) {
    		result = true;
        }
        stmt.close();
        return result;
    }
	public static boolean checkCourseId(Connection con, String courseId) throws SQLException {//강의 존재 여부 확인
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT course_id FROM course WHERE course_id = '" + courseId +"';");
    	boolean result = false;
    	
    	if(rset.next()) {
    		result = true;
        }
        stmt.close();
        return result;
    }
	public static boolean checkRoomId(Connection con, int roomId) throws SQLException {//강의 존재 여부 확인
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT room_id FROM room WHERE room_id = '" + roomId +"';");
    	boolean result = false;
    	
    	if(rset.next()) {
    		result = true;
        }
        stmt.close();
        return result;
    }
	public static boolean checkLecturerId(Connection con, String lecturerId) throws SQLException {//교강사 존재 여부 확인
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT lecturer_id FROM lecturer WHERE lecturer_id = '" + lecturerId +"';");
    	boolean result = false;
    	
    	if(rset.next()) {
    		result = true;
        }
        stmt.close();
        return result;
    }
	public static boolean checkSameTime(Connection con, String begin, String end, int roomId) throws SQLException{
		Statement stmt = con.createStatement();
		boolean result = false;
		ResultSet rset = stmt.executeQuery("SELECT time.timebegin, time.timeend FROM time, class WHERE time.class_id=class.class_id and class.room_id='"+roomId+"';");
		int time2 = calculateTime(begin);
		if(rset.next()){
			int timebegin = rset.getInt(1);
			int timeend = rset.getInt(2);
			if(timebegin<=time2&&time2<timeend){
				result=true;
			}
		}
		stmt.close();
		return result;
	}
	public static boolean checkTime(Connection con, String studentId, int classId, int timeId) throws SQLException {
		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		boolean result = false;
		ResultSet rset = stmt.executeQuery("SELECT time.timebegin FROM time WHERE time.time_id='"+timeId+"' and time.class_id='"+classId+"';");
		if(rset.next()){
			int classtime = rset.getInt(1);
			
			ResultSet rset2 = stmt2.executeQuery("SELECT time.timebegin, time.timeend FROM time, courseenrolled WHERE time.time_id=courseenrolled.time_id and courseenrolled.student_id='"+studentId+"';");
			while(rset2.next()){
				int timebegin = rset2.getInt(1);
				int timeend = rset2.getInt(2);
				if(timebegin<=classtime&&classtime<timeend){
					result=true;
					break;
				}
			}
		}
		stmt.close();
		stmt2.close();
		return result;
	}
	public static boolean checkMajor(Connection con, int majorId) throws SQLException{
		Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT major_id FROM major WHERE major_id = '" + majorId +"';");
    	boolean result = false;
    	if(rset.next())
    		result=true;
        stmt.close();
        return result;
	}
    public static boolean checkTakes(Connection con, String studentId, int classId) throws SQLException {
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT class_id FROM courseenrolled WHERE student_id = '" + studentId +"';");
    	boolean result = false;
    	while(rset.next()) {
    		if(rset.getInt(1)==classId)
    			result = true;
    	}
        stmt.close();
        return result;
    }  
    public static void insertTakes(Connection con, String studentId, int classId, int timeId) throws SQLException {
        con.setAutoCommit(false);
        
        try {
        	Statement stmt1 = con.createStatement();
        	Statement stmt2 = con.createStatement();

        	int current_credits;
        	int credits;
        	String course_name;
        	
        	ResultSet rset1 = stmt1.executeQuery("SELECT current_credits FROM student WHERE student_id = '" + studentId +"';");
        	if(rset1.next()){
        		current_credits = rset1.getInt(1) ;
        		
        		ResultSet rset2 = stmt2.executeQuery("SELECT credit, name FROM class WHERE class_id = '" + classId +"';");
            	if(rset2.next()){
            		
            		credits = rset2.getInt(1);
            		course_name = rset2.getString(2);
            	   	if(current_credits + credits <= 20){ // 20 학점 이하만 수강할수 있도록 제한
            		
            	   		PreparedStatement stmt = con.prepareStatement("INSERT INTO courseenrolled (student_id, class_id, time_id) VALUES(?,?,?);");
            	   		
            	   		stmt.setString(1, studentId);
            	   		stmt.setInt(2, classId);
            	   		stmt.setInt(3, timeId);
            	   		stmt.execute();
            	   		stmt.close();
				        System.out.println(course_name+" 등록이 완료되었습니다\n");
            	   	}
            	   	else{
				        System.out.println("수강 가능 학점을 초과하였습니다 (최대 20학점까지 수강가능)\n");
				        
				        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            	   	}
            	}
        	}
	        
        	stmt1.close();
        	stmt2.close();
	        con.commit();
	        con.setAutoCommit(true);
        } catch (SQLException e) {
        	e.printStackTrace();;
        }
    }
    public static void insertToTime(Connection con, int classId, int period, String begin, String end) throws SQLException{
    	con.setAutoCommit(false);
    	try{
    		PreparedStatement stmt = con.prepareStatement("INSERT INTO time (class_id, period, begin, end, timebegin, timeend) VALUES(?,?,?,?,?,?);");
    	
    		int timebegin = calculateTime(begin);
    		int timeend = calculateTime(end);
    		stmt.setInt(1, classId);
    		stmt.setInt(2, period);
    		stmt.setString(3, begin);
    		stmt.setString(4, end);
    		stmt.setInt(5, timebegin);
    		stmt.setInt(6, timeend);
    		stmt.execute();
    		stmt.close();
    		con.commit();
    		con.setAutoCommit(true);
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    public static void insertToClass(Connection con, int classId, String courseId, int majorId, String lecturerId, int personMax, int roomId) throws SQLException {
		con.setAutoCommit(false);
		int opened = 2014;
		try {
			PreparedStatement stmt = con.prepareStatement("INSERT INTO class VALUES(?,?,?,?,?,?,?,?,?,?,?);");
			Statement stmt2 = con.createStatement();
			ResultSet rset = stmt2.executeQuery("SELECT class_no, name, major_id, year, credit FROM class WHERE course_id = '"+courseId+"';");
			if(rset.next()){
				int classNo = rset.getInt(1);
				String title = rset.getString(2);
				int year = rset.getInt(4);
				int credit = rset.getInt(5);
				stmt.setInt(1, classId);
		        stmt.setInt(2, classNo);
		        stmt.setString(3, courseId);
		        stmt.setString(4, title.toUpperCase());
		        stmt.setInt(5, majorId);
		        stmt.setInt(6, year);
		        stmt.setInt(7, credit);
		        stmt.setString(8, lecturerId);
		        stmt.setInt(9, personMax);
		        stmt.setInt(10, opened);
		        stmt.setInt(11, roomId);
		        stmt.execute();
			}
	        stmt.close();
	        
	        con.commit();
	        con.setAutoCommit(true);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
    }
    public static void deleteFromTime(Connection con, int classId, int period) throws SQLException {
    	Statement stmt = con.createStatement();
    	stmt.execute("DELETE 
		              FROM time 
					  WHERE time.class_id = '"+classId+"' and time.period = '"+period+"';");
    	stmt.close();
    }
    public static void deleteFromClass(Connection con, int classId) throws SQLException {
    	Statement stmt = con.createStatement();
    	stmt.execute("DELETE 
		              FROM class 
					  WHERE class.class_id = '"+classId+"';");
    	stmt.close();
    }
    public static void deleteFromCourseenrolled(Connection con, int classId) throws SQLException {
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	ResultSet rset = stmt2.executeQuery("SELECT student_id FROM courseenrolled WHERE class_id='"+classId+"';");
    	stmt.execute("DELETE FROM courseenrolled WHERE courseenrolled.class_id = '"+classId+"';");
    	while(rset.next()){
    		updateCurrentCredits(con, rset.getString(1));
    	}
    	stmt.close();
    	stmt2.close();
    }
    public static void deleteTakes(Connection con,String studentId, int classId) throws SQLException {
    	Statement stmt = con.createStatement();
    	Statement stmt_takes = con.createStatement();
    	
    	stmt.execute("DELETE FROM courseenrolled WHERE courseenrolled.class_id = '" + classId +"' and courseenrolled.student_id = '" + studentId + "';");
    	
        stmt.close();
        stmt_takes.close();
    }
    public static void updateCurrentCreditsCourse(Connection con, int classId) throws SQLException{
    	
    }
    public static void updateCurrentCredits(Connection con, String studentId) throws SQLException {        
        
        try {
        	int tot_cred=0;
        	Statement stmt1 = con.createStatement();
        	
        	ResultSet rset = stmt1.executeQuery("SELECT class.credit FROM student, courseenrolled, class WHERE courseenrolled.student_id = student.student_id and" +
        			" class.class_id = courseenrolled.class_id and student.student_id = '" + studentId +"';");
        	
        	if(rset.next()) {
        		do { //수강생당 모든 수강강좌 출력
        			tot_cred = tot_cred + rset.getInt(1);
        			} while(rset.next());
        		}
        	PreparedStatement stmt2 = con.prepareStatement("UPDATE student SET current_credits = ? WHERE student_id = '"+ studentId + "';");
        	stmt2.setInt(1, tot_cred);
        	stmt2.execute();
        	
        	stmt1.close();
        	stmt2.close();
        }catch (SQLException e) {
        	e.printStackTrace();
        }
    }
    public static void tmpTimeUpdate(Connection con) throws SQLException{
    	int i=1;
    	int time2;
    	Statement stmt1 = con.createStatement();
    	ResultSet rset = stmt1.executeQuery("SELECT end FROM time;");
    	
    	while(rset.next()){
    		String time = rset.getString(1);
    		time2=calculateTime(time);
    		PreparedStatement stmt2 = con.prepareStatement("UPDATE time SET timeend = ? WHERE time_id='"+i+"';");
    		stmt2.setInt(1, time2);
    		stmt2.execute();
    		i+=1;
    		stmt2.close();
    	}
    	stmt1.close();
    }
    public static int calculateTime(String begin){
    	int timebegin=0;
    	String date;
    	String time;
    	String mins;
    	date=begin.substring(0, 1);
    	switch(date){
    		case "월":
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "화":
    			timebegin+=1440;
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "수":
    			timebegin+=2880;
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "목":
    			timebegin+=4320;
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "금":
    			timebegin+=5760;
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "토":
    			timebegin+=7200;
    			time = begin.substring(1, 3);
        		timebegin+=timeSwitch(time);
        		mins = begin.substring(4);
        		if(mins.equals("30")){
        			timebegin+=30;
        		}
        		else
        			timebegin+=0;
        		break;
    		case "NO":
    			timebegin+=0;
    			break;
    	}
    	return timebegin;
    	
    }
    public static int timeSwitch(String time){
    	int timebegin=0;
    	switch(time){
		case "00":
			timebegin+=0;
			break;
		case "01":
			timebegin+=60;
			break;
		case "02":
			timebegin+=120;
			break;
		case "03":
			timebegin+=180;
			break;
		case "04":
			timebegin+=240;
			break;
		case "05":
			timebegin+=300;
			break;
		case "06":
			timebegin+=360;
			break;
		case "07":
			timebegin+=420;
			break;
		case "08":
			timebegin+=480;
			break;
		case "09":
			timebegin+=540;
			break;
		case "10":
			timebegin+=600;
			break;
		case "11":
			timebegin+=660;
			break;
		case "12":
			timebegin+=720;
			break;
		case "13":
			timebegin+=780;
			break;
		case "14":
			timebegin+=840;
			break;
		case "15":
			timebegin+=900;
			break;
		case "16":
			timebegin+=960;
			break;
		case "17":
			timebegin+=1020;
			break;
		case "18":
			timebegin+=1080;
			break;
		case "19":
			timebegin+=1140;
			break;
		case "20":
			timebegin+=1200;
			break;
		case "21":
			timebegin+=1260;
			break;
		case "22":
			timebegin+=1320;
			break;
		case "23":
			timebegin+=1380;
			break;
    	}
    	return timebegin;
    }
    public static int showTime(Connection con, int classId) throws SQLException{ 
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT time_id, begin, end FROM time WHERE class_id='"+classId+"';");
    	Scanner sc = new Scanner(System.in);
    	int timeid[] = new int[4];
    	int i=1;
    	int select;
    	while(rset.next()){
    		timeid[i]=rset.getInt(1);
    		System.out.println(i+"."+"시작 시간 : "+rset.getString(2)+" 종료 시간 : "+rset.getString(3));
    		i+=1;
    	}
    	stmt.close();
    	System.out.print("원하는 수업 시간을 선택하세요. : ");
    	select = sc.nextInt();
    	if(select==1)
    		return timeid[1];
    	else if(select==2)
    		return timeid[2];
    	else
    		return timeid[3];
    }
    public static int showTime2(Connection con, int classId) throws SQLException{
    	Statement stmt = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT period, begin, end FROM time WHERE class_id='"+classId+"';");
    	Scanner sc = new Scanner(System.in);
    	int select;
    	while(rset.next()){
    		System.out.println(rset.getInt(1)+"."+"시작 시간 : "+rset.getString(2)+" 종료 시간 : "+rset.getString(3));
    	}
    	stmt.close();
    	System.out.print("원하는 수업 시간을 선택하세요. : ");
    	select = sc.nextInt();
    	return select;
    }
    public static void showMajorStatistic(Connection con, int majorId) throws SQLException {
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	ResultSet rset2 = stmt2.executeQuery("SELECT name FROM major WHERE major_id='"+majorId+"';");
    	ResultSet rset = stmt.executeQuery("SELECT class.name, class.opened FROM major, class WHERE major.major_id=class.major_id and class.major_id='"+majorId+"';");
    	if(rset2.next())
    		System.out.print("전공 : ");
    		System.out.println(rset2.getString(1));
    	while(rset.next()){
    		System.out.println("과목명 : "+rset.getString(1)+" 설강연도 : "+rset.getInt(2));
    	}
    	stmt.close();
    	stmt2.close();
    }
    public static void timeTable(Connection con, String studentId) throws SQLException{
    	Statement stmt = con.createStatement();
    	Statement stmt2 = con.createStatement();
    	ResultSet rset = stmt.executeQuery("SELECT time.timebegin FROM courseenrolled, time WHERE time.time_id=courseenrolled.time_id and courseenrolled.student_id='"+studentId+"';");
    	int max = 10000;
    	int timebegin[] = new int[]{max, max, max, max, max, max, max, max, max, max};
    	int i=0;
    	while(rset.next()){//timebegin값을 timebegin배열에 저장해서 오름차순으로 정렬한다. 그리고 그에 맞는 수업들을 차례로 출력해준다. 
    		timebegin[i]=rset.getInt(1);
    		i+=1;
    	}
    	stmt.close();
    	Arrays.sort(timebegin);
    	for(int j=0;j<i;j++){
    		ResultSet rset2 = stmt2.executeQuery("SELECT time.begin, time.end, class.name, class.room_id FROM time, class, courseenrolled WHERE courseenrolled.time_id=time.time_id and courseenrolled.class_id=class.class_id and courseenrolled.student_id='"+studentId+"' and time.timebegin='"+timebegin[j]+"';");
    	
    		if(rset2.next()){
    		
    		System.out.println("시간 : "+rset2.getString(1)+" ~ "+rset2.getString(2));
    		System.out.println("수업명 : "+rset2.getString(3));
    		System.out.println("강의실 : "+rset2.getInt(4));
    		System.out.println("*********************");
    		}
    	}
    	//stmt.close();
    	stmt2.close();
    	
    }
}
