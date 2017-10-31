//
//  TVSAuthDelegate.h
//  TVSAccountSDK
//
//  Created by RincLiu on 15/08/2017.
//  Copyright © 2017 TENCENT. All rights reserved.
//

#ifndef TVSAuthDelegate_h
#define TVSAuthDelegate_h

#import <Foundation/Foundation.h>

/*! 
 * @typedef TVSAuthEvent
 * @brief TVS 授权回调事件
 */
typedef NS_ENUM(NSUInteger, TVSAuthEvent) {
    /*!
     * @brief 获取微信 Token
     */
    TVSAuthEventFetchWXToken,
    /*!
     * @brief 刷新微信 Token
     */
    TVSAuthEventRefreshWXToken,
    /*!
     * @brief 验证 QQ token
     */
    TVSAuthEventVerifyQQToken,
    /*!
     * @brief 获取 TVSID
     */
    TVSAuthEventFetchId
};

/*!
 * @protocol TVSAuthDelegate
 * @brief 授权相关回调协议
 * @warning 必须实现本协议，否则无法收到相关事件回调
 */
@protocol TVSAuthDelegate <NSObject>

@required
/*!
 * @brief 事件回调
 * @param event 事件类型
 * @param success 是否成功
 */
-(void)TVSAuthEvent:(TVSAuthEvent)event Success:(BOOL)success;

@end


#endif /* AuthorizeListener_h */
