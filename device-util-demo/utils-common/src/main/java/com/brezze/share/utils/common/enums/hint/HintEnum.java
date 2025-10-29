package com.brezze.share.utils.common.enums.hint;

public interface HintEnum {

    //ZH,EN,FR,IT,DE
    /**
     * 中文
     */
    String LANGUAGE_ZH = "ZH";

    /**
     * 英文
     */
    String LANGUAGE_EN = "EN";

    /**
     * 法语
     */
    String LANGUAGE_FR = "FR";

    /**
     * 意大利语
     */
    String LANGUAGE_IT = "IT";

    /**
     * 德语
     */
    String LANGUAGE_DE = "DE";

    /**
     * 荷兰语
     */
    String LANGUAGE_NL = "NL";

    /**
     * 俄语
     */
    String LANGUAGE_RU = "RU";

    /**
     * 葡萄牙语
     */
    String LANGUAGE_PT = "PT";

    /**
     * 西班牙语
     */
    String LANGUAGE_ES = "ES";

    /**
     * 斯洛文尼亚语
     */
    String LANGUAGE_SL = "SL";

    /**
     * 希腊语
     */
    String LANGUAGE_EL = "EL";

    /**
     * 克罗地亚语
     */
    String LANGUAGE_HR = "HR";

    /**
     * 泰语
     */
    String LANGUAGE_TH = "TH";

    /**
     * 老挝语
     */
    String LANGUAGE_LO = "LO";

    /**
     * 乌克兰语
     */
    String LANGUAGE_UK = "UK";


    String getCode();

    String getZhDescription();

    String getEnDescription();

    String getNlDescription();

    String getFrDescription();

    String getItDescription();

    String getDeDescription();

    String getRuDescription();

    String getPtDescription();

    String getEsDescription();

    String getSlDescription();

    String getElDescription();

    String getHrDescription();

    String getThDescription();

    String getLoDescription();

    String getUkDescription();

    /**
     * 获取code并转化为整数
     *
     * @return
     */
    default Integer getIntCode() {
        return Integer.parseInt(getCode());
    }

    /**
     * 根据语言获取对应描述 默认英文
     *
     * @param language
     * @return
     */
    default String getMsg(String language, Object... params) {
        String msg = "";
        if (language == null || language.isEmpty()) {
            return msg;
        }
        switch (language.toUpperCase()) {
            case HintEnum.LANGUAGE_ZH: {
                msg = String.format(getZhDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_NL: {
                msg = String.format(getNlDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_FR: {
                msg = String.format(getFrDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_IT: {
                msg = String.format(getItDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_DE: {
                msg = String.format(getDeDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_RU: {
                msg = String.format(getRuDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_PT: {
                msg = String.format(getPtDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_ES: {
                msg = String.format(getEsDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_SL: {
                msg = String.format(getSlDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_EL: {
                msg = String.format(getElDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_HR: {
                msg = String.format(getHrDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_TH: {
                msg = String.format(getThDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_LO: {
                msg = String.format(getLoDescription(), params);
                break;
            }
            case HintEnum.LANGUAGE_UK: {
                msg = String.format(getUkDescription(), params);
                break;
            }
            default: {
                msg = String.format(getEnDescription(), params);
                break;
            }
        }

        return msg;
    }


}
