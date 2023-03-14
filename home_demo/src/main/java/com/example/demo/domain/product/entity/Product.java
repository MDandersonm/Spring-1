package com.example.demo.domain.product.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 128, nullable = false)
    private String productName;

    @Column(length = 32, nullable = false)
    private String writer;

    @Lob
    private String content;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ImageResource> imageResourceList = new ArrayList<>();

    private Integer price;

    @CreationTimestamp
    private Date regDate;

    @UpdateTimestamp
    private Date updDate;

    public void setImageResource(ImageResource imageResource) {
        imageResourceList.add(imageResource);
        imageResource.setProduct(this);
    }

    public void setImageResourceList(List<ImageResource> imageResourceList1) {
        imageResourceList.addAll(imageResourceList1);
    //인자로 Collection 객체를 받고 그 Collection에 있는 아이템들을 리스트에 모두 추가합니다.

        for (int i = 0; i < imageResourceList1.size(); i++) {
            imageResourceList1.get(i).setProduct(this);
        }
    }
}
