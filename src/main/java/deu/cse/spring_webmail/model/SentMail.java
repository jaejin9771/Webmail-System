package deu.cse.spring_webmail.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SentMail {

    @Getter @Setter private int id;
    @Getter @Setter private String sender;
    @Getter @Setter private String recipient;
    @Getter @Setter private String reference;
    @Getter @Setter private String mailName;
    @Getter @Setter private String mailBody;
    @Getter @Setter private String fileName;
    @Getter @Setter private Timestamp date;

    private String jdbcUrl;
    private String userName;
    private String password;
    private String jdbcDriver;

    public SentMail() {
        log.debug("SentMail 기본 생성자 호출됨");
    }

    public SentMail(String jdbcUrl, String userName, String password, String jdbcDriver) {
        this.jdbcUrl = jdbcUrl;
        this.userName = userName;
        this.password = password;
        this.jdbcDriver = jdbcDriver;
    }
    
    public boolean Save() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, userName, password);
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO sentmail (sender, recipient, reference, mailname, mail_body, file_name, savedate) " +
                 "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)"
             )) {

            pstmt.setString(1, this.sender);
            pstmt.setString(2, this.recipient);
            pstmt.setString(3, this.reference);
            pstmt.setString(4, this.mailName);
            pstmt.setString(5, this.mailBody);
            pstmt.setString(6, this.fileName);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SentMail> getSentMailList(String userid) {
        List<SentMail> mailList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
      
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, userName, password);

            String sql = "SELECT id, sender, recipient, reference, mailname, mailbody, filename, savedate " +
                         "FROM sentmail WHERE sender = ? ORDER BY savedate DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SentMail mail = new SentMail(); 
                mail.setId(rs.getInt("id"));
                mail.setSender(rs.getString("sender"));
                mail.setRecipient(rs.getString("recipient"));
                mail.setReference(rs.getString("reference"));
                mail.setMailName(rs.getString("mailname"));
                mail.setMailBody(rs.getString("mailbody"));
                mail.setFileName(rs.getString("filename"));
                mail.setDate(rs.getTimestamp("savedate"));
                mailList.add(mail);
            }

        } catch (Exception e) {
            log.error("보낸 메일 조회 실패", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                log.error("자원 해제 실패", ex);
            }
        }
        return mailList;
    }
}