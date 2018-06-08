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
            throw new IOException("��Ч��dataId");
        }

        handlerBatchConfigGet(request, response, group, dataIds);
    }

    private void handlerBatchConfigGet(HttpServletRequest request, HttpServletResponse response, String group, String dataIds) throws ServletException, IOException {
        // �����׳����쳣, �����һ��500����, ���ظ�sdk, sdk�Ὣ500�����¼����־��
        if (!StringUtils.hasLength(dataIds)) {
            throw new IllegalArgumentException("������ѯ, dataIds����Ϊ��");
        }
        // group������������ÿһ�����ݶ���ͬ, ����Ҫ��forѭ����������ж�
        if (!StringUtils.hasLength(group)) {
            throw new IllegalArgumentException("������ѯ, group����Ϊ�ջ��߰����Ƿ��ַ�");
        }

        // �ֽ�dataId
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
                // ��ѯ���ݿ�
                String filePath = diskService.getFilePath(dataId, group);
                if (filePath == null) {
                    // û���쳣, ˵����ѯ�ɹ�, �����ݲ�����, ���ò����ڵ�״̬��
                    configInfoEx.setStatus(Constants.BATCH_QUERY_NONEXISTS);
                    configInfoEx.setMessage("query data does not exist");
                } else {
                    // û���쳣, ˵����ѯ�ɹ�, �������ݴ���, ���ô��ڵ�״̬��
                    String content = FileUtils.getFileContent(filePath);
                    configInfoEx.setContent(content);
                    configInfoEx.setStatus(Constants.BATCH_QUERY_EXISTS);
                    configInfoEx.setMessage("query success");
                }
            } catch (Exception e) {
                log.error("������ѯ, �ڲ�ѯ���dataIdʱ����, dataId=" + dataId + ",group=" + group, e);
                // �����쳣, �����쳣״̬��
                configInfoEx.setStatus(Constants.BATCH_OP_ERROR);
                configInfoEx.setMessage("query error: " + e.getMessage());
            }
        }

        String json = "";
        try {
            json = JSONUtils.serializeObject(configInfoExList);
        } catch (Exception e) {
            log.error("������ѯ������л�����, json=" + json, e);
        }

        response.setContentType("application/text;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.close();
    }
}
