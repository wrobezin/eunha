package io.github.wrobezin.eunha.app.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.DeltaType;
import io.github.wrobezin.eunha.data.entity.document.Page;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/7 17:30
 */
public class DifferenceUtils {
    private static List<String> cutBodyToLines(Page page) {
        String body = page.getBody();
        body = body.replace("\r\n", "\n");
        body = body.replace("\r", "\n");
        return Arrays.asList(body.split("\n"));
    }

    private static List<Difference> diff(List<String> lines1, List<String> lines2) throws DiffException {
        return DiffUtils.diff(lines1, lines2)
                .getDeltas()
                .stream()
                .filter(delta -> !DeltaType.EQUAL.equals(delta.getType()))
                .map(delta -> Difference.builder()
                        .changeType(delta.getType().name())
                        .sourcePosition(delta.getSource().getPosition())
                        .sourceLineNum(delta.getSource().getLines().size())
                        .targetPosition(delta.getTarget().getPosition())
                        .targetLineNum(delta.getTarget().getLines().size())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<Difference> diff(String s1, String s2) throws DiffException {
        return diff(Collections.singletonList(s1), Collections.singletonList(s2));
    }

    public static List<Difference> diff(Page newPage, Page oldPage) {
        List<Difference> result = new LinkedList<>();
        try {
            diff(oldPage.getTitle(), newPage.getTitle())
                    .forEach(difference -> {
                        difference.setInfoType(Difference.TITLE);
                        result.add(difference);
                    });
            diff(cutBodyToLines(oldPage), cutBodyToLines(newPage))
                    .forEach(difference -> {
                        difference.setInfoType(Difference.BODY);
                        result.add(difference);
                    });
        } catch (DiffException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        Page document1 = Page.builder()
                .title("test")
                .body("test1\ntest\ntest")
                .build();
        Page document2 = Page.builder()
                .title("test")
                .body("test2\ntest\ntest\ntest2")
                .build();
        System.out.println(diff(document1, document2));
    }
}
