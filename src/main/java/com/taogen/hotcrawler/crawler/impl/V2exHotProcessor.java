package com.taogen.hotcrawler.crawler.impl;

import com.taogen.hotcrawler.crawler.HotProcess;
import com.taogen.hotcrawler.entity.Info;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:sites.properties")
public class V2exHotProcessor implements HotProcess
{
    private static final Logger log = LoggerFactory.getLogger(V2exHotProcessor.class);

    @Value("${sites[0].domain}")
    private String V2EX_DOMAIN;

    @Value("${sites[0].hotPageUrl}")
    private String HOT_PAGE_URL;

    @Value("${sites[0].itemKey}")
    private String ITEM_KEY;


    @Override
    public List<Info> getHotList()
    {
        List<Info> list = new ArrayList<>();

        // document
        Document doc = null;
        try {
            doc = Jsoup.connect(HOT_PAGE_URL).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // elements
        Elements elements = doc.getElementsByClass(ITEM_KEY);

        int i = 0;
        for (Element element : elements)
        {
            Elements elements1 = element.getElementsByTag("a");
            String infoTitle = elements1.html();
            String infoUrl = V2EX_DOMAIN + elements1.attr("href");
            String id = String.valueOf(++i);
            list.add(new Info(id, infoTitle, infoUrl));
        }
        return list;
    }
}
