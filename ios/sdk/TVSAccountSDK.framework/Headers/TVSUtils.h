//
//  TVSUtils.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 11/4/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * @class TVSUtils
 * @brief TVS 工具类
 */
@interface TVSUtils : NSObject

/*!
 * @brief 获取 GUID
 * @param oldGuid 旧的 guid，如果不为空则进行校验，如果合法则原样返回，如果不合法则返回新的合法的 guid
 * @param business 业务名称，必须不为空
 * @param productId 选填
 * @param dsn 选填
 * @param qua 选填
 * @param imei 选填
 * @param lc 选填
 * @param mac 选填
 * @param handler 回调，NSString* 参数即为 返回的 guid
 */
+(void)pickGuidWithOldGuid:(NSString*)oldGuid business:(NSString*)business productId:(NSString*)productId dsn:(NSString*)dsn qua:(NSString*)qua imei:(NSString*)imei lc:(NSString*)lc mac:(NSString*)mac handler:(void(^)(NSString*))handler;

/*!
 * @brief 启动微信小程序
 * @param username 小程序用户名
 * @param path 小程序路径
 * @param type 小程序类型(0:正式，1:测试，2:体验)
 * @param handler 回调，NSString 参数对应 JsApi navigateBackApplication 中的 extraData 字段数据
 * @return 是否成功
 */
+(BOOL)launchMiniProgramWithUsername:(NSString*)username path:(NSString*)path type:(NSInteger)type handler:(void(^)(NSString*))handler;

@end
