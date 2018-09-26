//
//  TVSSpeaker.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 6/9/2018.
//  Copyright © 2018 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * @class TVSSpeaker
 * @brief TVS 音箱信息管理
 */
@interface TVSSpeaker : NSObject

/*!
 * @brief 实例化
 * @param productId
 * @param dsn
 * @return 实例
 */
-(instancetype)initWithProductId:(NSString*)productId dsn:(NSString*)dsn;

/*!
 * @brief 设置昵称
 * @warning 必须确保已登录！！
 * @param nickName 设备昵称
 * @param handler 回调
 */
-(void)setNickName:(NSString*)nickName handler:(void(^)(BOOL))handler;

/*!
 * @brief 获取昵称
 * @warning 必须确保已登录！！
 * @param handler
 */
-(void)getNickNameWithHandler:(void(^)(NSString*))handler;

@end
