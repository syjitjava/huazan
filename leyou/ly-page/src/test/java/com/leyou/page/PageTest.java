package com.leyou.page;

import com.leyou.LyPageApplication;
import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyPageApplication.class)
public class PageTest {

    @Autowired
    private PageService pageService;
    @Test
    public void createHtml(){
        List<Long> list = Arrays.asList(3L, 4L, 5L, 6L, 7L, 8L, 9L);
        for (Long aLong : list) {
            pageService.createItemHtml(aLong);
        }

    }
}
