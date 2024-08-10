package com.wizz.fi.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: cenyh
 * @Date: 2023/07/11
 */
@Slf4j
public class BeanUtil {
    public static <S> Page<S> iPage2Page(IPage<S> source) {
        return iPage2Page(source, source.getRecords());
    }

    public static <S, T> Page<T> iPage2Page(IPage<S> source, List<T> data) {
        Page<T> result = new Page<>();

        result.setCurrent(source.getCurrent());
        result.setSize(source.getSize());
        result.setPages(source.getPages());
        result.setRecords(data);
        result.setTotal(source.getTotal());

        return result;
    }

    public static <S, T> Page<T> iPage2Page(IPage<S> source, Class<T> VOClassType) {
        Page<T> result = new Page<>();

        result.setCurrent(source.getCurrent());
        result.setSize(source.getSize());
        result.setPages(source.getPages());

        List<T> voRecords = source.getRecords().stream().map(record -> PO2VO(record, VOClassType)).collect(Collectors.toList());
        result.setRecords(voRecords);
        result.setTotal(source.getTotal());

        return result;
    }

    public static <S, T> List<T> POList2VOList(List<S> POList, Class<T> VOClassType) {
        return POList.stream()
                .map(PO -> PO2VO(PO, VOClassType))
                .collect(Collectors.toList());
    }

    public static <S, T> T PO2VO(S PO, Class<T> VOClassType) {
        try {
            if (Objects.isNull(PO)) {
                return null;
            }

            T VO = VOClassType.newInstance();

            BeanUtils.copyProperties(PO, VO);

            return VO;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("POJOConverter.PO2VO instance error", e);

            return null;
        }
    }

    public static <S, T> T DTO2PO(S DTO, Class<T> POClassType) {
        try {
            if (Objects.isNull(DTO)) {
                return null;
            }

            T PO = POClassType.newInstance();

            BeanUtils.copyProperties(DTO, PO);

            return PO;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("POJOConverter.DTO2PO instance error", e);

            return null;
        }
    }
}