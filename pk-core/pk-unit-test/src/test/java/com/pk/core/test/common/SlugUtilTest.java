package com.pk.core.test.common;

import com.pk.core.common.util.SlugUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugUtilTest {

    @Test
    void removesVietnameseDiacriticsAndSpaces() {
        assertEquals("nhan-kim-cuong-18k", SlugUtil.slugify("Nhẫn Kim Cương 18K"));
    }

    @Test
    void convertsLetterDStroke() {
        assertEquals("dong-ho-dep", SlugUtil.slugify("Đồng hồ đẹp"));
    }

    @Test
    void collapsesSymbolsAndTrimsDashes() {
        assertEquals("day-chuyen-bac-925", SlugUtil.slugify("  --Dây chuyền / Bạc (925)!! "));
    }

    @Test
    void nullAndBlankGiveEmpty() {
        assertEquals("", SlugUtil.slugify(null));
        assertEquals("", SlugUtil.slugify("   "));
    }
}
