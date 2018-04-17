//
//  TVSAlarmManager.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 11/4/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * @brief TVS 闹钟(提醒)重复类型
 */
typedef NS_ENUM(NSInteger,TVSAlarmRepeatType) {
    /*!
     * @brief 每天重复
     */
    TVSAlarmRepeatTypeOnce,
    /*!
     * @brief 每天重复
     */
    TVSAlarmRepeatTypeDay,
    /*!
     * @brief 每周重复
     */
    TVSAlarmRepeatTypeWeek,
    /*!
     * @brief 每月重复
     */
    TVSAlarmRepeatTypeMonth,
    /*!
     * @brief 工作日重复
     */
    TVSAlarmRepeatTypeWorkday,
    /*!
     * @brief 非工作日重复
     */
    TVSAlarmRepeatTypeWeekend,
};


/*!
 * @class TVSAlarm
 * @brief TVS 闹钟(提醒)实体
 */
@interface TVSAlarm : NSObject

/*!
 * @brief 闹钟(提醒) id
 * @warning ‘修改’、‘删除’操作必须传!
 */
@property(nonatomic,assign) long alarmId;

/*!
 * @brief 闹钟(提醒) 时间
 * @warning ‘添加’、‘更新’操作必须传!
 */
@property(nonatomic,assign) long alarmTime;

/*!
 * @brief 闹钟(提醒) 内容
 */
@property(nonatomic,strong) NSString* alarmNote;

/*!
 * @brief 闹钟(提醒)
 * @warning ‘添加’操作必须传!
 */
@property(nonatomic,assign) TVSAlarmRepeatType alarmRepeatType;

/*!
 * @brief 设备 productId
 * @warning '增/删/改/查' 都必须传!
 */
@property(nonatomic,strong) NSString* deviceProductId;

/*!
 * @brief 设备 guid
 * @warning '增/删/改/查' 都必须传!
 */
@property(nonatomic,strong) NSString* deviceGuid;

@end


/*!
 * @class TVSAlarmManager
 * @brief TVS 云端闹钟(提醒)管理
 * @warning 必须确保已经调用 TVSAccount 登录授权!
 */
@interface TVSAlarmManager : NSObject

/*!
 * @brief 实例化
 * @param business 业务名
 * @param botKey bot 的 key
 * @param botToken bot 的 token
 * @return 实例
 */
-(instancetype)initWithBussiness:(NSString*)business botKey:(NSString*)botKey botToken:(NSString*)botToken;

/*!
 * @brief 查询所有闹钟(提醒)
 * @param deviceProductId 设备 productId
 * @param deviceGuid 设备 guid
 * @param handler 回调，NSArray* 参数为闹钟(提醒)列表
 */
-(void)queryAlarmsWithDeviceProductId:(NSString*)deviceProductId deviceGuid:(NSString*)deviceGuid handler:(void(^)(NSArray<TVSAlarm*>*))handler;

/*!
 * @brief 添加闹钟(提醒)
 * @param alarms 闹钟(提醒)列表
 * @param handler 回调，BOOL 参数表示是否成功
 */
-(void)addAlarms:(NSArray<TVSAlarm*>*)alarms handler:(void(^)(BOOL))handler;

/*!
 * @brief 修改闹钟(提醒)
 * @param alarms 闹钟(提醒)列表
 * @param handler 回调，BOOL 参数表示是否成功
 */
-(void)updateAlarms:(NSArray<TVSAlarm*>*)alarms handler:(void(^)(BOOL))handler;

/*!
 * @brief 删除闹钟(提醒)
 * @param alarms 闹钟(提醒)列表
 * @param handler 回调，BOOL 参数表示是否成功
 */
-(void)deleteAlarms:(NSArray<TVSAlarm*>*)alarms handler:(void(^)(BOOL))handler;

@end
