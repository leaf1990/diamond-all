package com.taobao.diamond.server.controller;

import com.taobao.diamond.common.Constants;
import com.taobao.diamond.domain.ConfigInfoEx;
import com.taobao.diamond.server.service.ConfigService;
import com.taobao.diamond.server.service.DiskService;
import com.taobao.diamond.utils.FileUtils;
import com.taobao.diamond.utils.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConfigBatchServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(ConfigBatchServlet.class);
    private static final long serialVersionUID = 4339468526746635388L;

    private DiskService diskService;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.diskService = (DiskService) webApplicationContext.getBean("diskService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        this.doGet(request, response);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String group = request.getParameter("group");
        String dataIds = request.getParameter("dataIds");

        if (!StringUtils.hasLength(dataIds)) {
            throw new IOException("无效的dataId");
        }

        handlerBatchConfigGet(request, response, group, dataIds);
    }

    private void handlerBatchConfigGet(HttpServletRequest request, HttpServletResponse response, String group, String dataIds) throws ServletException, IOException {
        // 这里抛出的异常, 会产生一个500错误, 返回给sdk, sdk会将500错误记录到日志中
        if (!StringUtils.hasLength(dataIds)) {
            throw new IllegalArgumentException("批量查询, dataIds不能为空");
        }
        // group对批量操作的每一条数据都相同, 不需要在for循环里面进行判断
        if (!StringUtils.hasLength(group)) {
            throw new IllegalArgumentException("批量查询, group不能为空或者包含非法字符");
        }

        // 分解dataId
        String[] dataIdArray = dataIds.split(Constants.LINE_SEPARATOR);
        group = group.trim();

        List<ConfigInfoEx> configInfoExList = new ArrayList<ConfigInfoEx>();
        for (String dataId : dataIdArray) {
            ConfigInfoEx configInfoEx = new ConfigInfoEx();
            configInfoEx.setDataId(dataId);
            configInfoEx.setGroup(group);
            configInfoExList.add(configInfoEx);
            try {
                if (org.apache.commons.lang.StringUtils.isBlank(dataId)) {
                    configInfoEx.setStatus(Constants.BATCH_QUERY_NONEXISTS);
                    configInfoEx.setMessage("dataId is blank");
                    continue;
                }
                // 查询数据库
                String filePath = diskService.getFilePath(dataId, group);
                if (filePath == null) {
                    // 没有异常, 说明查询成功, 但数据不存在, 设置不存在的状态码
                    configInfoEx.setStatus(Constants.BATCH_QUERY_NONEXISTS);
                    configInfoEx.setMessage("query data does not exist");
                } else {
                    // 没有异常, 说明查询成功, 而且数据存在, 设置存在的状态码
                    String content = FileUtils.getFileContent(filePath);
                    configInfoEx.setContent(content);
                    configInfoEx.setStatus(Constants.BATCH_QUERY_EXISTS);
                    configInfoEx.setMessage("query success");
                }
            } catch (Exception e) {
                log.error("批量查询, 在查询这个dataId时出错, dataId=" + dataId + ",group=" + group, e);
                // 出现异常, 设置异常状态码
                configInfoEx.setStatus(Constants.BATCH_OP_ERROR);
                configInfoEx.setMessage("query error: " + e.getMessage());
            }
        }

        String json = "";
        try {
            json = JSONUtils.serializeObject(configInfoExList);
        } catch (Exception e) {
            log.error("批量查询结果序列化出错, json=" + json, e);
        }

        response.setContentType("application/text;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.close();
    }
}
