package com.grc.item.controller;

import com.grc.common.BaseController;
import com.grc.common.JsonUtils;
import com.grc.item.domain.Item;
import com.grc.item.domain.ItemCategory;
import com.grc.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 郭若辰
 * @create 2017-03-01 16:17
 */
@RestController
public class ItemController extends BaseController {

    @Autowired
    ItemService itemService;

    /*
    根据id查询商品
     */
    @RequestMapping(value = "/{itemId}", method = RequestMethod.GET)
    public Item getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    /*
    查询商品列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> getItemsPageable(int page, int rows) {
        try {
            return easyUIDatagrid(itemService.getItemsPageable(page, rows));
        } catch (Exception e) {
            return badResponse(e.getMessage());
        }
    }

    /*
    选择商品类目
     */
    @RequestMapping(value = "/cat/list", method = RequestMethod.POST)
    public List<Map<String, Object>> getItemCategroy(@RequestParam(value = "id", defaultValue = "0") Long parentId) {
        List<Map<String, Object>> ret = new ArrayList<>();
        List<ItemCategory> list = itemService.getItemCategroy(parentId);
        try {
            for (ItemCategory itemCategory : list) {
                Map<String, Object> map = easyUITree(itemCategory.getId(), itemCategory.getName(), (itemCategory.getIsParent() == 1) ? "closed" : "open");
                ret.add(map);
            }
            return ret;
        } catch (Exception e) {
            ret.add(badResponse(e.getMessage()));
            return ret;
        }
    }

    /*
    上传图片
     */
    @RequestMapping(value = "/pic/upload", method = RequestMethod.POST)
    public String upload(MultipartFile uploadFile) {
        Map<String, Object> map;
        try {
            //访问图片的url
            String url = itemService.upload(uploadFile);
            map = KESuccess(url);
            //为了保证功能的兼容性（火狐浏览器），需要把结果转换为json格式的字符串
            String result = JsonUtils.objectToJson(map);
            return result;
        } catch (IOException e) {
            map = KEFail("文件" + uploadFile.getOriginalFilename() + "上传失败");
            return JsonUtils.objectToJson(map);
        }
    }

    /*
    新增商品
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Map<String, Object> insertItem(Item item, String desc) {
        try {
            Long id = itemService.insertItem(item, desc);
            return okResponse("商品 " + id + " 新增成功");
        } catch (Exception e) {
            return badResponse(e.getMessage());
        }
    }

    /*
    编辑商品
     */

}
