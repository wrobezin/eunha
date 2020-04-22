package io.github.wrobezin.eunha.push;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.enums.PushContactTypeEnum;
import io.github.wrobezin.eunha.push.mail.DingTalkService;
import io.github.wrobezin.eunha.push.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/20 17:31
 */
@Component
@Slf4j
public class PushHub {
    private final MailService mailService;
    private final DingTalkService dingTalkService;

    public PushHub(MailService mailService, DingTalkService dingTalkService) {
        this.mailService = mailService;
        this.dingTalkService = dingTalkService;
    }

    private String generatePushHtml(List<HyperLink> newPage, List<HyperLink> updatedPage, String ruleName) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>规则【").append(ruleName).append("】有新抓取结果，共获取新增页面").append(newPage.size()).append("个及更新页面").append(updatedPage.size()).append("个</h2>");
        if (newPage.size() > 0) {
            html.append("<h3>新增页面</h3><ur>");
            for (HyperLink link : newPage) {
                html.append("<li><a herf=\"")
                        .append(link.getUrl())
                        .append("\">")
                        .append(link.getAnchorText())
                        .append("</a></li>");
            }
            html.append("</ur><br>");
        }
        if (updatedPage.size() > 0) {
            html.append("<h3>更新页面</h3><ur>");
            for (HyperLink link : updatedPage) {
                html.append("<li><a herf=\"")
                        .append(link.getUrl())
                        .append("\">")
                        .append(link.getAnchorText())
                        .append("</a></li>");
            }
            html.append("</ur>");
        }
        return html.toString();
    }

    private String generatePushMarkdonw(List<HyperLink> newPage, List<HyperLink> updatedPage, String ruleName) {
        StringBuilder md = new StringBuilder();
        md.append("## 【").append(ruleName).append("】新抓取结果\n");
        md.append("共获取新增页面").append(newPage.size()).append("个及更新页面").append(updatedPage.size()).append("个\n");
        if (newPage.size() > 0) {
            md.append("### 新增页面\n");
            for (HyperLink link : newPage) {
                md.append("+ [")
                        .append(link.getAnchorText())
                        .append("](")
                        .append(link.getUrl())
                        .append(")\n");
            }
        }
        if (updatedPage.size() > 0) {
            md.append("### 更新页面\n");
            for (HyperLink link : updatedPage) {
                md.append("+ [")
                        .append(link.getAnchorText())
                        .append("](")
                        .append(link.getUrl())
                        .append(")\n");
            }
        }
        return md.toString();
    }

    public void push(List<CrawlResult> results, CustomizedRule rule) {
        List<HyperLink> newPage = new LinkedList<>();
        List<HyperLink> updatedPage = new LinkedList<>();
        for (CrawlResult result : results) {
            if (!result.getIsRuleMatched()) {
                continue;
            }
            if (result.getNewPage()) {
                newPage.add(new HyperLink(result.getPageInDb().getTitle(), result.getUrl()));
            } else if (result.getUpdated()) {
                updatedPage.add(new HyperLink(result.getPageInDb().getTitle(), result.getUrl()));
            }
        }
        if (newPage.size() == 0 && updatedPage.size() == 0) {
            return;
        }
        String html = generatePushHtml(newPage, updatedPage, rule.getName());
        String markdonw = generatePushMarkdonw(newPage, updatedPage, rule.getName());
        String dingTalkMessage = "{\n" +
                "    \"msgtype\": \"markdown\",\n" +
                "    \"markdown\": {\n" +
                "\"title\":\"【" + rule.getName() + "】新抓取结果\"," +
                "\"text\":\"" + markdonw + "\n\"" +
                "    }\n" +
                "}";
        rule.getPushContacts().forEach(contact -> {
            if (PushContactTypeEnum.EMAIL.equals(contact.getType())) {
                try {
                    mailService.sendMail(contact.getValue(), "【" + rule.getName() + "】有新抓取结果", html);
                } catch (Exception e) {
                    log.error("发送邮件出错", e);
                }
            } else if (PushContactTypeEnum.DING_TALK.equals(contact.getType())) {
                dingTalkService.sendMarkdownMessage(contact, dingTalkMessage);
            }
        });
    }
}
