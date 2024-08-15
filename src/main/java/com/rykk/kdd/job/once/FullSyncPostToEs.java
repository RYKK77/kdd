package com.rykk.kdd.job.once;

import com.rykk.kdd.model.entity.Post;
import com.rykk.kdd.service.PostService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步帖子到 es
 *

 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {
        List<Post> postList = postService.list();
        if (CollUtil.isEmpty(postList)) {
            return;
        }
    }
}
