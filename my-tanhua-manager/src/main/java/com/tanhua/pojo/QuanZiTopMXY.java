package com.tanhua.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_top")
public class QuanZiTopMXY implements Serializable {
    @Id
    private ObjectId id;

    private ObjectId publishId;//圈子id
    private Long userId;//发布人id
    private String managername;//管理员名称
    private Long created;

}
