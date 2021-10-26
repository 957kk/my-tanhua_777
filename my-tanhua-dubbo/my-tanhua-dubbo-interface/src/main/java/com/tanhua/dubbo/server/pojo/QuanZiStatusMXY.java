package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "quanzi_status")
public class QuanZiStatusMXY implements Serializable {
    @Id
    private ObjectId id;
    private ObjectId publishId;
    private String manager;
    private String status;
    private Long created;
}
