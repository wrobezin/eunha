package io.github.wrobezin.eunha.app.vo;

import io.github.wrobezin.eunha.data.entity.document.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/17 17:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {
    private String title;
    private String url;
    private String host;
    private String summary;
    private String body;
    private Integer version;
    private LocalDateTime updateTime;

    private static final Cleaner TAG_CLEANER = new Cleaner(Whitelist.none().addTags("em"));
    private static final int SUMMARY_WHOLE_LENGTH_LIMIT = 256;
    private static final int SUMMARY_LINE_LENGTH_LIMIT = 128;
    private static final int EM_END_TAG_LENGTH = 5;
    private static final int SUMMARY_SEGMET_WINDOW = 32 + EM_END_TAG_LENGTH;

    private String generateSummary(String body) {
        List<String> lines = Stream.of(body.split("\n"))
                .filter(line -> line.contains("<search-em>"))
                .map(line -> line.replace("<search-em>", "<em>"))
                .map(line -> line.replace("</search-em>", "</em>"))
                .map(Jsoup::parse)
                .map(TAG_CLEANER::clean)
                .map(Document::body)
                .map(Element::html)
                .map(line -> line.replace("\n", ""))
                .sorted((l1, l2) -> l2.length() - l1.length())
                .collect(Collectors.toList());
        int totalLength = 0;
        StringBuilder summary = new StringBuilder();
        for (String line : lines) {
            if (summary.length() > SUMMARY_WHOLE_LENGTH_LIMIT) {
                break;
            }
            if (line.length() > SUMMARY_LINE_LENGTH_LIMIT) {
                int firstEmStartPosition = line.indexOf("<em>");
                int start = Math.max(firstEmStartPosition - SUMMARY_SEGMET_WINDOW, 0);
                int firstEmEndPosition = line.indexOf("</em>");
                int secondEmStartPosition = line.indexOf("<em>", firstEmEndPosition);
                // 右边界不超过第二组em
                int end = secondEmStartPosition > 0 ? secondEmStartPosition : line.length();
                end = Math.min(firstEmEndPosition + SUMMARY_SEGMET_WINDOW, end);
                line = line.substring(start, end) + "……";
            }
            summary.append(line);
            summary.append("……");
        }
        return summary.toString();
    }

    public PageVO(Page page) {
        this.title = page.getTitle()
                .replace("<search-em>", "<em>")
                .replace("</search-em>", "</em>");
        this.body = page.getBody();
        this.summary = generateSummary(page.getBody());
        this.url = page.getUrl();
        this.host = page.getHost();
        this.version = page.getVersion();
        this.updateTime = page.getUpdateTime();
    }
}
