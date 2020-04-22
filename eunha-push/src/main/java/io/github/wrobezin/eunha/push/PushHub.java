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

    private static final int LIST_MAX_LENGTH = 10;

    public PushHub(MailService mailService, DingTalkService dingTalkService) {
        this.mailService = mailService;
        this.dingTalkService = dingTalkService;
    }

    private void addHtmlLinkList(String title, List<HyperLink> pages, StringBuilder html) {
        if (pages.size() > 0) {
            html.append("<h3>").append(title).append("</h3><ur>");
            for (int i = 0; i < pages.size(); i++) {
                if (i > LIST_MAX_LENGTH) {
                    html.append("<li>更多请前往系统查看</li>");
                    break;
                }
                html.append("<li><a herf=\"")
                        .append(pages.get(i).getUrl())
                        .append("\">")
                        .append(pages.get(i).getAnchorText())
                        .append("</a></li>");
            }
            html.append("</ur>");
        }
    }

    private String generatePushHtml(List<HyperLink> newPage, List<HyperLink> updatedPage, String ruleName) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>规则【").append(ruleName).append("】有新抓取结果，共获取新增页面").append(newPage.size()).append("个及更新页面").append(updatedPage.size()).append("个</h2>");
        html.append("<span>页面详情请前往系统后台查看</span>");
        addHtmlLinkList("新增页面", newPage, html);
        addHtmlLinkList("更新页面", updatedPage, html);
        return html.toString();
    }

    private void addMarkdownLinkList(String title, List<HyperLink> pages, StringBuilder md) {
        if (pages.size() > 0) {
            md.append("### ").append(title).append("\n");
            for (int i = 0; i < pages.size(); i++) {
                if (i > LIST_MAX_LENGTH) {
                    md.append("+ 更多请前往系统查看");
                    break;
                }
                md.append("+ [")
                        .append(pages.get(i).getAnchorText())
                        .append("](")
                        .append(pages.get(i).getUrl())
                        .append(")\n");
            }
        }
    }

    private String generatePushMarkdonw(List<HyperLink> newPage, List<HyperLink> updatedPage, String ruleName) {
        StringBuilder md = new StringBuilder();
        md.append("## 【").append(ruleName).append("】新抓取结果\n");
        md.append("共获取新增页面").append(newPage.size()).append("个及更新页面").append(updatedPage.size()).append("个\n");
        md.append("页面详情请前往[系统后台](#)查看\n");
        addMarkdownLinkList("新增页面", newPage, md);
        addMarkdownLinkList("更新页面", updatedPage, md);
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
