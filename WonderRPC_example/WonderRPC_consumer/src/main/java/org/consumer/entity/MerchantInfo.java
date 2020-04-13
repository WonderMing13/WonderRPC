package org.consumer.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wonder
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MerchantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 商品名称

     */
    private String merchantName;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品产地
     */
    private String merchantProduction;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 是否在售 0:在售 1;禁售
     */
    private Integer isSold;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime creatorTime;

    /**
     * 确认人
     */
    private String modifier;

    /**
     * 确认时间
     */
    private LocalDateTime modifiyTime;


}
