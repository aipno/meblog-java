package cn.iswxl.meblog.common.markdown;

import cn.iswxl.meblog.common.markdown.renderer.ImageNodeRenderer;
import cn.iswxl.meblog.common.markdown.renderer.LinkNodeRenderer;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Arrays;
import java.util.List;

public class MarkdownHelper {

    /**
     * Markdown 解析器
     */
    private final static Parser PARSER;
    /**
     * HTML 渲染器
     */
    private final static HtmlRenderer HTML_RENDERER;

    /*
      初始化
     */
    static {
        // Markdown 拓展
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(), // 表格拓展
                HeadingAnchorExtension.create(), // 标题锚点
                ImageAttributesExtension.create(),  // 图片宽高
                TaskListItemsExtension.create() // 任务列表
        );

        PARSER = Parser.builder().extensions(extensions).build();
        HTML_RENDERER = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(ImageNodeRenderer::new) // 自定义图片解析
                .nodeRendererFactory(LinkNodeRenderer::new) // 自定义超链接解析
                .build();
    }

    /**
     * 将 Markdown 转换成 HTML
     *
     */
    public static String convertMarkdown2Html(String markdown) {
        Node document = PARSER.parse(markdown);
        String html = HTML_RENDERER.render(document);
        // 使用Jsoup清洗HTML，只允许安全的标签和属性
        return sanitizeHtml(html);
    }

    /**
     * 清洗HTML，移除潜在的XSS攻击载体
     */
    private static String sanitizeHtml(String html) {
        // 创建白名单，只允许安全的HTML标签和属性
        Safelist safelist = new Safelist()
                // 基本标签
                .addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                        "colgroup", "dd", "del", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                        "hr", "i", "img", "ins", "kbd", "li", "mark", "ol", "p", "pre", "q",
                        "rp", "rt", "ruby", "s", "samp", "small", "span", "strong", "sub",
                        "sup", "table", "tbody", "td", "tfoot", "th", "thead", "time", "tr", "u", "ul")
                
                // a标签属性
                .addAttributes("a", "href", "title", "target")
                
                // img标签属性
                .addAttributes("img", "align", "alt", "height", "src", "title", "width", "class")
                
                // blockquote标签属性
                .addAttributes("blockquote", "cite")
                
                // code标签属性
                .addAttributes("code", "class")
                
                // div标签属性
                .addAttributes("div", "class")
                
                // h1-h6标签属性
                .addAttributes("h1", "class")
                .addAttributes("h2", "class")
                .addAttributes("h3", "class")
                .addAttributes("h4", "class")
                .addAttributes("h5", "class")
                .addAttributes("h6", "class")
                
                // li标签属性
                .addAttributes("li", "class")
                
                // ol标签属性
                .addAttributes("ol", "class")
                
                // p标签属性
                .addAttributes("p", "class")
                
                // pre标签属性
                .addAttributes("pre", "class")
                
                // span标签属性
                .addAttributes("span", "class")
                
                // table标签属性
                .addAttributes("table", "class", "style")
                
                // td标签属性
                .addAttributes("td", "class", "colspan", "rowspan")
                
                // th标签属性
                .addAttributes("th", "class", "colspan", "rowspan")
                
                // tr标签属性
                .addAttributes("tr", "class")
                
                // ul标签属性
                .addAttributes("ul", "class")
                
                // 设置协议白名单
                .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                .addProtocols("blockquote", "cite", "http", "https")
                .addProtocols("img", "src", "http", "https");

        // 使用自定义的白名单清洗HTML
        return Jsoup.clean(html, safelist);
    }

    public static void main(String[] args) {
        String markdown = """
                # 一级标题
                ## 二级标题
                """;
        System.out.println(MarkdownHelper.convertMarkdown2Html(markdown));
    }

}
