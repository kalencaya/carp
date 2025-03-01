/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.sliew.carp.module.datasource.service.impl;

import cn.sliew.carp.framework.common.codec.CodecUtil;
import cn.sliew.carp.framework.common.dict.datasource.CarpDataSourceType;
import cn.sliew.carp.framework.common.model.PageResult;
import cn.sliew.carp.framework.mybatis.DataSourceConstants;
import cn.sliew.carp.module.datasource.config.CarpGravitinoProperties;
import cn.sliew.carp.module.datasource.modal.DataSourceInfo;
import cn.sliew.carp.module.datasource.repository.entity.DsInfo;
import cn.sliew.carp.module.datasource.repository.entity.DsInfoVO;
import cn.sliew.carp.module.datasource.repository.mapper.DsInfoMapper;
import cn.sliew.carp.module.datasource.service.CarpDsInfoService;
import cn.sliew.carp.module.datasource.service.CarpGravitinoMetalakeService;
import cn.sliew.carp.module.datasource.service.convert.DsInfoConvert;
import cn.sliew.carp.module.datasource.service.convert.DsInfoVOConvert;
import cn.sliew.carp.module.datasource.service.dto.DsInfoDTO;
import cn.sliew.carp.module.datasource.service.param.DsInfoListParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.sliew.milky.common.check.Ensures.checkState;

@Service
public class CarpDsInfoServiceImpl extends ServiceImpl<DsInfoMapper, DsInfo> implements CarpDsInfoService {

    @Autowired
    private CarpGravitinoProperties properties;
    @Autowired
    private CarpGravitinoMetalakeService gravitinoMetalakeService;

    @Override
    public PageResult<DsInfoDTO> list(DsInfoListParam param) {
        Page<DsInfo> page = new Page<>(param.getCurrent(), param.getPageSize());
        Page<DsInfoVO> dsInfoPage = baseMapper.list(page, param.getDsType(), param.getName());
        PageResult<DsInfoDTO> result = new PageResult<>(dsInfoPage.getCurrent(), dsInfoPage.getSize(), dsInfoPage.getTotal());
        List<DsInfoDTO> dsInfoDTOS = DsInfoVOConvert.INSTANCE.toDto(dsInfoPage.getRecords());
        result.setRecords(dsInfoDTOS);
        return result;
    }

    @Override
    public List<DsInfoDTO> listByType(CarpDataSourceType type) {
        List<DsInfoVO> dsInfoVOS = baseMapper.listByTypes(type);
        return DsInfoVOConvert.INSTANCE.toDto(dsInfoVOS);
    }

    @Override
    public DsInfoDTO selectOne(Long id, boolean decrypt) {
        DsInfoVO vo = baseMapper.getById(id);
        checkState(vo != null, () -> "data source info not exists for id: " + id);
        DsInfoDTO dsInfoDTO = DsInfoVOConvert.INSTANCE.toDto(vo);
        if (decrypt) {
            Map<String, Object> props = new HashMap<>();
            for (Map.Entry<String, Object> entry : dsInfoDTO.getProps().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (Objects.nonNull(value)
                        && value instanceof String
                        && CodecUtil.isEncryptedStr((String) value)) {
                    props.put(key, CodecUtil.decrypt((String) value));
                } else {
                    props.put(key, value);
                }
            }
            dsInfoDTO.setProps(props);
        }
        return dsInfoDTO;
    }

    @Override
    public boolean add(DataSourceInfo dataSource) {
        DsInfo record = DsInfoConvert.INSTANCE.toDo(dataSource.toDsInfo());
        boolean save = save(record);
        if (save) {
            gravitinoMetalakeService.tryAddCatalog(properties.getMetalake(), selectOne(record.getId(), true));
        }
        return save;
    }

    @Override
    public boolean update(Long id, DataSourceInfo dataSource) {
        DsInfo record = DsInfoConvert.INSTANCE.toDo(dataSource.toDsInfo());
        record.setId(id);
        boolean saveOrUpdate = saveOrUpdate(record);
        if (saveOrUpdate) {
            gravitinoMetalakeService.tryUpdateCatalog(properties.getMetalake(), selectOne(record.getId(), true));
        }
        return saveOrUpdate;
    }

    @Override
    public boolean deleteById(Long id) {
        DsInfoDTO dsInfoDTO = selectOne(id, true);
        boolean remove = removeById(id);
        if (remove) {
            gravitinoMetalakeService.tryDeleteCatalog(properties.getMetalake(), dsInfoDTO);
        }
        return remove;
    }

    @Transactional(rollbackFor = {Exception.class}, transactionManager = DataSourceConstants.TRANSACTION_MANAGER_FACTORY)
    @Override
    public boolean deleteBatch(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }
        List<DsInfoDTO> dsInfoDTOS = ids.stream().map(id -> selectOne(id, true)).collect(Collectors.toList());
        boolean remove = removeBatchByIds(ids);
        if (remove) {
            for (DsInfoDTO dsInfoDTO : dsInfoDTOS) {
                gravitinoMetalakeService.tryDeleteCatalog(properties.getMetalake(), dsInfoDTO);
            }
        }
        return remove;
    }
}
