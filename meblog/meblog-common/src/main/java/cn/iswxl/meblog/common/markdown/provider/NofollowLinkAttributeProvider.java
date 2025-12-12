package cn.iswxl.meblog.common.markdown.provider;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class NofollowLinkAttributeProvider implements AttributeProvider {

    /**
     * 网站域名（从配置文件中获取）
     */
    @Value("${site.external-link-domain}")
    private String domain;

    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
        if (node instanceof Link linkNode) {
            // 获取链接地址
            String href = linkNode.getDestination();
            // 如果链接不是自己域名，则添加 rel="nofollow" 属性
            if (!href.contains(domain)) {
                attributes.put("rel", "nofollow");
            }
        }
    }
}
