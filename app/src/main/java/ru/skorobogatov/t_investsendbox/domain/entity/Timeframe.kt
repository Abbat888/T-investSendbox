package ru.skorobogatov.t_investsendbox.domain.entity

enum class Timeframe(val limit: Int) {

    CANDLE_INTERVAL_1_MIN(limit = 2400),
    CANDLE_INTERVAL_2_MIN(limit = 1200),
    CANDLE_INTERVAL_3_MIN(limit = 750),
    CANDLE_INTERVAL_5_MIN(limit = 2400),
    CANDLE_INTERVAL_15_MIN(limit = 2400),
    CANDLE_INTERVAL_10_MIN(limit = 1200),
    CANDLE_INTERVAL_30_MIN(limit = 1200),
    CANDLE_INTERVAL_HOUR(limit = 2400),
    CANDLE_INTERVAL_2_HOUR(limit = 2400),
    CANDLE_INTERVAL_4_HOUR(limit = 700),
    CANDLE_INTERVAL_DAY(limit = 2400),
    CANDLE_INTERVAL_WEEK(limit = 300),
    CANDLE_INTERVAL_MONTH(limit = 120)
}