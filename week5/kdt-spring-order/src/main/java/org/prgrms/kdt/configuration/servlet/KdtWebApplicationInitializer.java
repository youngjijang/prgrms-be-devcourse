package org.prgrms.kdt.configuration.servlet;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        var servletRegistration = servletContext.addServlet("test",new TestServlet());
        servletRegistration.addMapping("/*");
        servletRegistration.setLoadOnStartup(-1);
    }
}
