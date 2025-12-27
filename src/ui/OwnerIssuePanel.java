package ui;

import service.IssueService;

import javax.swing.*;
import java.awt.*;

public class OwnerIssuePanel extends JPanel {

    private final SuCoView suCoView;

    public OwnerIssuePanel(IssueService issueService) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        suCoView = new SuCoView(issueService);
        add(suCoView, BorderLayout.CENTER);
    }

    public void reloadData() {
        suCoView.reloadData();
    }
}
