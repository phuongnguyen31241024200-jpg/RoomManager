package service;



import model.IssueReport;



import model.IssueStatus;



import java.util.ArrayList;



import java.util.List;







public class IssueReportServiceImpl implements IssueService {




    private static final List<IssueReport> issues = new ArrayList<>();







    @Override



    public void addIssue(IssueReport issue) {



        if (issue != null) {



            issues.add(issue);



        }



    }




    @Override



    public List<IssueReport> getAllIssues() {



        // Trả về bản sao của List để đảm bảo an toàn dữ liệu



        return new ArrayList<>(issues);



    }







    @Override



    public int getOpenIssueCount() {



        int count = 0;



        for (IssueReport issue : issues) {




            if (issue.getStatus() == IssueStatus.PENDING) {



                count++;



            }



        }



        return count;



    }



    @Override



    public void clearAll() {



        // Xóa sạch bộ nhớ RAM

        issues.clear();



    }



}