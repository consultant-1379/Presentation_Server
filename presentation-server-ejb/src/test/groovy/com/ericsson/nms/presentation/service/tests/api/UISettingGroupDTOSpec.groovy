package com.ericsson.nms.presentation.service.tests.api

import com.ericsson.nms.presentation.service.tests.base.AbstractCacheSpec
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO
import spock.lang.Unroll

/**
 * Test specification for the UISettingGroupDTO
 */
class UISettingGroupDTOSpec extends AbstractCacheSpec {

    @Unroll
    def "Size should be calculated correctly for bean with id: #id and value: #value"() {

        when: "add a single entry to the cache"
            def uiSettingGroupDTO = new UISettingGroupDTO([new UISettingBean(id, value)])
            def calcSize = uiSettingGroupDTO.calculateSize()

        then: "calculate the data size"
            calcSize == size

        where: "with the given data"
            id   | value | size
            null | null  | 0     // 0 + 0 + 0
            null | "x"   | 2     // 0 + 0 + 2
            "x"  | null  | 4     // 2 + 2 + 0
            "x"  | "x"   | 6     // 2 + 2 + 2
    }

}
