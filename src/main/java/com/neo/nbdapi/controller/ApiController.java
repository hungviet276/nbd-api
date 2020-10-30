//package com.neo.nbdapi.controller;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
////import org.springframework.web.multipart.MultipartFile;
//
//import com.neo.nbdapi.config.SqlProperties;
////import com.neo.api.model.ObjectId;
//import com.neo.nbdapi.services.ObjectService;
////import com.neo.api.service.TokenService;
//import com.neo.nbdapi.utils.Constants.ConstantParams;
//
//@Transactional
//@RestController
//@RequestMapping("/neo")
//// @PropertySources({ @PropertySource("classpath:static/sql.properties") })
//public class ApiController {
//    @Autowired
//    private SqlProperties sqlProperties;
//
//    @Autowired
//    private ObjectService objectService;
//
//    final String CONNECTION_STRING = "constr";
//    final String PARAM = ".param";
//
//    @RequestMapping("/")
//    @ResponseBody
//    public String welcome() {
//        return "Welcome to RestTemplate Example.";
//    }
//
//    @RequestMapping(value = "/api/{path}")
//    public void apiGateway(@PathVariable("path") String path, HttpServletRequest request,
//                           HttpServletResponse response) {
//        RequestDispatcher rd = request.getRequestDispatcher("/neo" + path);
//        System.out.println("call qua api gateway" + path);
//        try {
//            rd.forward(request, response);
//        } catch (ServletException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * @param cac cau query dang nay chu yeu xuat ra du lieu dang result set va chi
//     * la lay du lieu ra do vay method se la get va truyen param va lay ra du lieu
//     * dang resultset
//     */
//    @SuppressWarnings("unchecked")
//    @GetMapping("/qry")
//    @ResponseBody
//    public <Type> Type qry(@RequestParam Map<Object, Object> params) {
//        System.out.println(params.toString());
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        List<Map<Object, Object>> list = objectService.qry(params, sql, order);
//        if (list == null) {
//            return null;
//        } else {
//            return ((Type) list);
//        }
//    }
//
//    /*
//     * @param cac cau query dang nay chu yeu xuat ra du lieu dang result set va chi
//     * la lay du lieu ra do vay method se la get va truyen param va lay ra du lieu
//     * dang resultset, khac query o cho, no truyen vao cac function hay procedure
//     */
//    @SuppressWarnings("unchecked")
//    @GetMapping("/ref")
//    @ResponseBody
//    public <Type> Type ref(@RequestParam Map<Object, Object> params) {
//        System.out.println(params.toString());
//
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        List<Map<Object, Object>> list = objectService.ref(params, sql, order);
//        if (list == null) {
//            return null;
//        } else {
//            return ((Type) list);
//        }
//    }
//
//    /*
//     * @param cac cau query dang nay xuat phat tu cac function hay procedure va tra
//     * ra du lieu 1 dang kieu du lieu nao do nhu vay can call va tra ra du lieu
//     */
//    @RequestMapping(value = "/get-val", method = { RequestMethod.POST, RequestMethod.GET })
//    @ResponseBody
//    public Object valParam(@RequestParam Map<Object, Object> params) {
//        Object result = null;
//        System.out.println(params.toString());
//
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        // escape html character
//        for (Entry<Object, Object> entry : params.entrySet()) {
//            String finalString = entry.getValue().toString();
//            finalString = StringUtils.replaceEach(finalString, new String[] { "&", "\"", "<", ">" },
//                    new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
//            params.put(entry.getKey(), (Object) finalString);
//        }
//        result = objectService.val(params, sql, order);
//        return result;
//    }
//
//    @RequestMapping(value = "/post-val", method = RequestMethod.POST)
//    @ResponseBody
//    public Object valBody(@RequestBody Map<Object, Object> params) {
//        Object result = null;
//        System.out.println(params.toString());
//
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        // escape html character
//        for (Entry<Object, Object> entry : params.entrySet()) {
//            String finalString = entry.getValue().toString();
//            finalString = StringUtils.replaceEach(finalString, new String[] { "&", "\"", "<", ">" },
//                    new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
//            params.put(entry.getKey(), (Object) finalString);
//        }
//        result = objectService.val(params, sql, order);
//        return result;
//    }
//
//    /*
//     * @param cac cau query dang nay la dang update, insert, delete cac bang table,
//     * nen can ban method post va truyen du lieu di
//     */
//    @RequestMapping(value = "/update", method = { RequestMethod.POST })
//    @ResponseBody
//    public Object updateParam(@RequestParam Map<Object, Object> params) {
//        Object result = 0;
//        System.out.println(params.toString());
//
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        // escape html character
//        for (Entry<Object, Object> entry : params.entrySet()) {
//            String finalString = entry.getValue().toString();
//            finalString = StringUtils.replaceEach(finalString, new String[] { "&", "\"", "<", ">" },
//                    new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
//            params.put(entry.getKey(), (Object) finalString);
//        }
//        result = objectService.update(params, sql, order);
//        return result;
//    }
//
//    /*
//     * khi 1 request ma day len dang body se vao day
//     */
//    @RequestMapping(value = "/updates", method = RequestMethod.POST)
//    @ResponseBody
//    public Object updateBody(@RequestBody Map<Object, Object> params) {
//        Object result = 0;
//        System.out.println(params.toString());
//
//        String sql = sqlProperties.getProperty(params.get(CONNECTION_STRING).toString());
//        Logger.getLogger("ws").info(params.toString() + "| SQL: ======>" + sql);
//        String[] order = null;
//        if (sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM) != null) {
//            order = sqlProperties.getProperty(params.get(CONNECTION_STRING) + PARAM).split(ConstantParams.SPLIT_CHARACTER);
//        } else {
//            order = new String[0];
//        }
//
//        // escape html character
//        for (Entry<Object, Object> entry : params.entrySet()) {
//            String finalString = entry.getValue().toString();
//            finalString = StringUtils.replaceEach(finalString, new String[] { "&", "\"", "<", ">" },
//                    new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
//            params.put(entry.getKey(), (Object) finalString);
//        }
//        result = objectService.update(params, sql, order);
//        return result;
//    }
//
//}
