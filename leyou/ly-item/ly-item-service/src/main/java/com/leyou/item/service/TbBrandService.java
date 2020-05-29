package com.leyou.item.service;

        import com.baomidou.mybatisplus.extension.service.IService;
        import com.leyou.common.vo.PageResult;
        import com.leyou.item.dto.BrandDTO;
        import com.leyou.item.entity.TbBrand;

        import java.util.List;


/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbBrandService extends IService<TbBrand> {

    PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(TbBrand tbBrand, List<Long> cids);

    void updateBrand(TbBrand tbBrand, List<Long> cids);

    BrandDTO findBrandById(Long id);

    void deleteBrandById(Long id);

    List<BrandDTO> findBrandByCategoryId(Long cid);

    List<BrandDTO> findBrandListByIds(List<Long> ids);
}
