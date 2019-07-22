package com.taogen.hotcrawler.commons.task;

import com.taogen.hotcrawler.api.constant.SiteProperties;
import com.taogen.hotcrawler.api.service.BaseService;
import com.taogen.hotcrawler.commons.crawler.HotProcessor;
import com.taogen.hotcrawler.commons.entity.Info;
import com.taogen.hotcrawler.commons.repository.InfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
@EnableScheduling
public class CrawlerTask
{
    private static final Logger log = LoggerFactory.getLogger(CrawlerTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SiteProperties siteProperties;


    @Scheduled(fixedRateString = "${crawler.task.fixedRate}", initialDelayString = "${crawler.task.initialDelay}")
    public void crawlHotList()
    {
        List<SiteProperties.SiteInfo> siteList = siteProperties.getSites();
        if (siteList != null)
        {
            for (SiteProperties.SiteInfo site : siteList)
            {
                new Thread(()->
                {
                    HotProcessor hotProcess = (HotProcessor) baseService.getBean(site.getProcessorName());
                    List<Info> infoList = hotProcess.crawlHotList();
                    log.info("crawler " + site.getName()+ " hot list size: " + infoList.size());
                    infoRepository.removeByTypeId(site.getId());
                    infoRepository.saveAll(infoList, site.getId());
                }).start();
            }
        }
    }
}
