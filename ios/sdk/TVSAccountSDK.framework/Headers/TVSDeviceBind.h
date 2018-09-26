//
//  TVSDeviceBind.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 17/11/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TVSAccount.h"



/*!
 * @class TVSPushDevice
 * @brief TVS Push 设备
 */
@interface TVSPushDevice : NSObject

/*!
 * @brief productId TVS 后台申请的 appid:accessToken
 */
@property(nonatomic,copy) NSString* productId;

/*!
 * @brief DSN 设备序列号
 */
@property(nonatomic,copy) NSString* DSN;

/*!
 * @brief pushId
 */
@property(nonatomic,copy) NSString* pushId;

/*!
 * @brief pushIdExtra
 */
@property(nonatomic,copy) NSString* pushIdExtra;

/*!
 * @brief guid
 */
@property(nonatomic,copy) NSString* guid;

/*!
 * @brief deviceId 设备ID
 */
@property(nonatomic,copy) NSString* deviceId;

/*!
 * @brief deviceName 设备名
 */
@property(nonatomic,copy) NSString* deviceName;

/*!
 * @brief deviceType 设备类型
 */
@property(nonatomic,copy) NSString* deviceType;

/*!
 * @brief deviceSerial 设备系列
 */
@property(nonatomic,copy) NSString* deviceSerial;

/*!
 * @brief deviceOEM 设备厂商
 */
@property(nonatomic,copy) NSString* deviceOEM;

/*!
 * @brief deviceOEMUrl 设备品牌图标
 */
@property(nonatomic,copy) NSString* deviceOEMUrl;

/*!
 * @brief deviceMark 设备备注
 */
@property(nonatomic,copy) NSString* deviceMark;

/*!
 * @brief QUA
 */
@property(nonatomic,copy) NSString* QUA;

/*!
 * @brief IMEI
 */
@property(nonatomic,copy) NSString* IMEI;

/*!
 * @brief LC
 */
@property(nonatomic,copy) NSString* LC;

/*!
 * @brief MAC
 */
@property(nonatomic,copy) NSString* MAC;

/*!
 * @brief QIMEI
 */
@property(nonatomic,copy) NSString* QIMEI;

/*!
 * @brief enrollTime 注册时间
 */
@property(nonatomic,assign) long long enrollTime;

/*!
 * @brief bindTime 绑定时间
 */
@property(nonatomic,assign) long long bindTime;

/*!
 * @brief extra 扩展信息
 */
@property(nonatomic,strong) NSDictionary* extra;

@end



/*!
 * @class TVSDeviceBind
 * @brief TVS 设备发现/绑定接口
 */
@interface TVSDeviceBind : NSObject

/*!
 * @brief 获得 TVS 设备发现类单例对象
 * @return TVS 设备发现类实例
 */
+(instancetype)shared;

/*!
 * @brief 绑定 APP
 * @warning 必须确保已登录
 * @param guid
 * @param deviceToken
 * @param bundleId
 * @param qua
 * @param extra
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)bindAppWithGuid:(NSString*)guid deviceToken:(NSString*)deviceToken bundleId:(NSString*)bundleId qua:(NSString*)qua extra:(NSDictionary*)extra handler:(void(^)(BOOL))handler;

/*!
 * @brief 绑定设备
 * @warning 必须确保已登录
 * @param pushDevice 设备信息，其中 productId 和 DSN(设备序列号)必传，其他字段根据需要透传
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)bindDevice:(TVSPushDevice*)pushDevice handler:(void(^)(BOOL))handler;

/*!
 * @brief 解绑设备
 * @warning 必须确保已登录
 * @param productId 设备 productId
 * @param dsn 设备序列号
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)unbindDeviceWithProductId:(NSString*)productId dsn:(NSString*)dsn handler:(void(^)(BOOL))handler;

/*!
 * @brief 查询绑定过的 push 设备列表
 * @warning 必须确保已登录
 * @param handler 回调
 */
-(void)queryPushDevicesWithHandler:(void(^)(NSArray<TVSPushDevice*>*))handler;


/*!
 * @brief 根据设备信息反查绑定的账号信息
 * @param productId 设备 productId
 * @param dsn 设备序列号
 * @param handler 回调，TVSAccountInfo 为账号信息
 */
-(void)queryBoundAccountWithDeviceProductId:(NSString*)productId dsn:(NSString*)dsn handler:(void(^)(TVSAccountInfo*))handler;

@end
