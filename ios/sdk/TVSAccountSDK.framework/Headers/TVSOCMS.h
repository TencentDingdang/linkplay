//
//  TVSOCMS.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 21/8/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * @class TVSAISpeechItem
 * @brief TVS 音色配置
 */
@interface TVSAISpeechItem : NSObject

/*!
 * @brief 音色ID
 */
@property(nonatomic,strong) NSString* speechID;

/*!
 * @brief 音色名称
 */
@property(nonatomic,strong) NSString* speechName;

/*!
 * @brief AISpeechType枚举值
 */
@property(nonatomic,strong) NSString* speechEnum;

/*!
 * @brief 是否默认音色
 */
@property(nonatomic,strong) NSString* isDefaultOption;

@end

/*!
 * @class TVSOCMS
 * @brief TVS 音色配置管理
 */
@interface TVSOCMS : NSObject

/*!
 * @brief 实例化
 * @return 实例
 */
+(instancetype)shared;

/*!
 * @brief 获取 bot 音色配置
 * @param productId
 * @param handler 回调，参数为音色配置列表
 */
-(void)getBotAISpeechOptionWithProductId:(NSString*)productId handler:(void(^)(NSArray<TVSAISpeechItem*>*))handler;

/*!
 * @brief 获取设备音色
 * @warning 必须登录后调用！！
 * @param deviceGUID 设备GUID
 * @param productId
 * @param DSN
 * @param handler 回调，参数为音色配置列表
 */
-(void)getDeviceAISpeechWithDeviceGUID:(NSString*)deviceGUID productId:(NSString*)productId DSN:(NSString*)dsn handler:(void(^)(TVSAISpeechItem*))handler;

/*!
 * @brief 设置设备音色
 * @warning 必须登录后调用！！
 * @param speechID 音色ID
 * @param productId
 * @param DSN
 * @param handler 回调，BOOL参数表示是否设置成功
 */
-(void)setDeviceAISpeechId:(NSString*)speechID productId:(NSString*)productId DSN:(NSString*)dsn handler:(void(^)(BOOL))handler;

@end
