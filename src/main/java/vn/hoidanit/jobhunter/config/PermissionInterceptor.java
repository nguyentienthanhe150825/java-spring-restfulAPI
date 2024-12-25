package vn.hoidanit.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();

                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                    }
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                }
            }
        }

        return true;
    }

    // https://www.baeldung.com/spring-mvc-handlerinterceptor
    // Request -> Spring Security -> Interceptor -> Controller -> Service...
}


// Explain: Transactional có thể gây lỗi: LazyInitializationException
// Hibernate Session: đại diện cho 1 phiên làm việc thao tác giữa ứng dụng và cơ sở dữ liệu
// Chu kỳ của 1 Hibernate Session:
// Mở: Khi thực hiện thao tác với cơ sở dữ liệu
// Mở: Hibernate Session hoạt động trong phạm vi của Transaction
// Đóng: Sau khi transaction kết thúc, Session sẽ đóng

// Sau khi Session đóng sẽ không thể truy cập đươc vào Lazy Loaded
// Khi đoạn code vào tới Controller -> Service -> repository thì
// Mỗi method sẽ được Java Spring tự động tạo 1 Transaction chứa Hibernate Session sẽ luôn được hoạt động xuyên suốt 1 method đó

// Nhưng khi ở Interceptor (Java Spring sẽ ko tự động tạo transaction để duy trì Hibernate Session)
// -> Vì vậy Hibernate Session MỞ khi truy vấn this.userService.handleGetUserByUsername(email) thực hiện (truy vấn lần 1)
// -> Sau đó truy vấn hoàn tất, Hibernate Session sẽ đóng do ko có transaction
// -> có nghĩa là Hibernate Session sẽ đóng sau khi thực hiện đoạn code: handleGetUserByUsername

// -> Nên khi đoạn code role.getPermissions() được thực thi
// -> tức là đang cố truy cập vào cơ sở dữ liệu (truy vấn lần 2) dựa vào Role để lấy Permissions
// -> nhưng mối quan hệ ràng buộc giữa Role và Permission là: FetchType.LAZY 
// => sẽ xảy ra lỗi LazyInitializationException (do Hibernate Session đã đóng)


// =====> Giải pháp: @Transactional: 
// Tạo 1 Hibernate Session duy trì xuyên suốt method preHandle (và có thể truy vấn tới database nhiều lần mà ko sợ Hibernate Session đóng)
