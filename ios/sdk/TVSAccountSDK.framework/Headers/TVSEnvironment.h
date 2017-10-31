//
//  TVSEnvironment.h
//  TVSAccountSDK
//
//  Created by Rinc Liu on 30/10/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TVSEnvironment : NSObject

+(instancetype)shared;

@property (nonatomic,assign) BOOL test;

@end
