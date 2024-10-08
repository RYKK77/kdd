package com.rykk.kdd.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rykk.kdd.model.dto.app.AppAddRequest;
import com.rykk.kdd.model.dto.app.AppQueryRequest;
import com.rykk.kdd.model.entity.App;
import com.rykk.kdd.model.entity.User;
import com.rykk.kdd.model.vo.AppVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 应用服务
 *
 */
public interface AppService extends IService<App> {

    /**
     * 校验数据
     *
     * @param app
     * @param add 对创建的数据进行校验
     */
    void validApp(App app, boolean add);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);
    
    /**
     * 获取应用封装
     *
     * @param app
     * @param request
     * @return
     */
    AppVO getAppVO(App app, HttpServletRequest request);

    /**
     * 分页获取应用封装
     *
     * @param appPage
     * @param request
     * @return
     */
    Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request);

    /**
     * 创建应用
     * @param appAddRequest APP相关描述
     * @param loginUser  当前登录用户
     * @return AppID
     */
    Long addApp(AppAddRequest appAddRequest, User loginUser);
}
