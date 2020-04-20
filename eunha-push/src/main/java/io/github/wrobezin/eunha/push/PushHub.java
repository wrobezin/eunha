package io.github.wrobezin.eunha.push;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.enums.PushContactTypeEnum;
import io.github.wrobezin.eunha.push.mail.MailService;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/20 17:31
 */
@Component
public class PushHub {
    private final MailService mailService;

    public PushHub(MailService mailService) {
        this.mailService = mailService;
    }

    private String generatePushHtml(List<HyperLink> newPage, List<HyperLink> updatedPage, String ruleName) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>规则【").append(ruleName).append("】有新抓取结果，共获取新页面").append(newPage.size()).append("个及更新页面").append(updatedPage.size()).append("个</h2>");
        if (newPage.size() > 0) {
            html.append("<h3>新页面</h3><ur>");
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
        rule.getPushContacts().forEach(contact -> {
            if (PushContactTypeEnum.EMAIL.equals(contact.getType())) {
                mailService.sendMail(contact.getValue(), "【" + rule.getName() + "】有新抓取结果", html);
            }
        });
    }
}
