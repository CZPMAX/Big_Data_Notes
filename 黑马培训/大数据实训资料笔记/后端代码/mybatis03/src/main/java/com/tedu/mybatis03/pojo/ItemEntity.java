package com.tedu.mybatis03.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

//实体类
@TableName("item")
public class ItemEntity {
   Integer id; //id列
    Integer categoryId;//category_id列
    String name;//name列
    Integer price;//price列
    String image;//image列
    String itemDesc;//item_desc列

 public Integer getId() {
  return id;
 }

 public void setId(Integer id) {
  this.id = id;
 }

 public Integer getCategoryId() {
  return categoryId;
 }

 public void setCategoryId(Integer categoryId) {
  this.categoryId = categoryId;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public Integer getPrice() {
  return price;
 }

 public void setPrice(Integer price) {
  this.price = price;
 }

 public String getImage() {
  return image;
 }

 public void setImage(String image) {
  this.image = image;
 }

 public String getItemDesc() {
  return itemDesc;
 }

 public void setItemDesc(String itemDesc) {
  this.itemDesc = itemDesc;
 }
}
