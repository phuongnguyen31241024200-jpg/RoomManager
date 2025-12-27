package service;



import model.IssueReport;

import java.util.List;



public interface IssueService {



    void addIssue(IssueReport issue);



    List<IssueReport> getAllIssues();



    int getOpenIssueCount();




    void clearAll();

}