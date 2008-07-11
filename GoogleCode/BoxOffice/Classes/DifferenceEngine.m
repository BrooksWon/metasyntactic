//
//  DifferenceEngine.m
//  BoxOffice
//
//  Created by Cyrus Najmabadi on 5/3/08.
//  Copyright 2008 Metasyntactic. All rights reserved.
//

#import "DifferenceEngine.h"
#import "Application.h"

#define MaxLength 128

@implementation DifferenceEngine

+ (DifferenceEngine*) engine {
    return [[[DifferenceEngine alloc] init] autorelease];
}

- (id) initWithAddCost:(NSInteger) add
            deleteCost:(NSInteger) delete
            switchCost:(NSInteger) switch_
         transposeCost:(NSInteger) transpose {
    if (self = [super init]) {
        addCost = add;
        deleteCost = delete;
        switchCost = switch_;
        transposeCost = transpose;
        
        for (NSInteger i = 0; i < MaxLength; i++) {
            costTable[i][0] = (i * deleteCost);
            costTable[0][i] = (i * addCost);
        }
    }
    
    return self;
}

- (id) init {
    return [self initWithAddCost:1
                      deleteCost:1
                      switchCost:1
                   transposeCost:1];
}

- (BOOL) initializeFrom:(NSString*) S
                     to:(NSString*) T
          withThreshold:(NSInteger) threshold {
    if (S == nil || T == nil) {
        return NO;
    }
    
    cached_S_length = [S length];
    cached_T_length = [T length];
    
    if (cached_T_length > MaxLength || cached_S_length > MaxLength) {
        return NO;
    }
    
    costThreshold = threshold;
    
    if (costThreshold >= 0) {
        if (deleteCost > 0) {
            NSInteger minimumTLength = cached_S_length - (costThreshold * deleteCost);
            
            if (cached_T_length < minimumTLength) {
                return NO;
            }
        }
        
        if (addCost > 0) {
            NSInteger minimumSLength = cached_T_length - (costThreshold * addCost);
            
            if (cached_S_length < minimumSLength) {
                return NO;
            }
        }
    }
    
    return YES;
}

NSInteger DE_min(NSInteger x, NSInteger y) {
    return x < y ? x : y;
}

- (NSInteger) editDistanceFrom:(NSString*) from
                            to:(NSString*) to {
    return [self editDistanceFrom:from to:to withThreshold:-1];
}

- (NSInteger) editDistanceFrom:(NSString*) from
                            to:(NSString*) to
                 withThreshold:(NSInteger) threshold {
    if ([self initializeFrom:from to:to withThreshold:threshold] == NO) {
        return NSIntegerMax;
    }
    
    unichar S[MaxLength];
    unichar T[MaxLength];
    
    [from getCharacters:S];
    [to getCharacters:T];
    
    for (NSInteger i = 1; i < cached_S_length; i++) {
        BOOL rowIsUnderThreshold = (costThreshold < 0);
        
        for (NSInteger j = 1; j < cached_T_length; j++) {
            const NSInteger adds = 1;
            const NSInteger deletes = 1;
            NSInteger switches = (S[(i - 1)] == T[(j - 1)]) ? 0 : 1;
            
            NSInteger totalDeleteCost = costTable[i - 1][j] + (deletes * deleteCost);
            NSInteger totalAddCost = costTable[i][j - 1] + (adds * addCost);
            NSInteger totalSwitchCost = costTable[i - 1][j - 1] + (switches * switchCost);
            NSInteger cost = DE_min(totalDeleteCost, DE_min(totalAddCost, totalSwitchCost));
            
            if (i >= 2 && j >= 2) {
                NSInteger transposes = 1 + ((S[(i - 1)] == T[j]) ? 0 : 1) + 
                ((S[i] == T[(j - 1)]) ? 0 : 1);
                NSInteger tCost = costTable[i - 2][j - 2] + (transposes * transposeCost);
                
                costTable[i][j] = DE_min(cost, tCost);
            } else {
                costTable[i][j] = cost;
            }
            
            if (costThreshold >= 0) {
                rowIsUnderThreshold |= (cost <= costThreshold);
            }
        }
        
        if (!rowIsUnderThreshold) {
            return NSIntegerMax;
        }
    }
    
    NSInteger cost = costTable[cached_S_length - 1][cached_T_length - 1];    
    
    if (cost > 1) {
        if ([from rangeOfString:to options:NSCaseInsensitiveSearch].location != NSNotFound ||
            [to rangeOfString:from options:NSCaseInsensitiveSearch].location != NSNotFound) {
            return 1;
        }
    }
    
    return cost;
}

+ (BOOL) areSimilar:(NSString*) s1
              other:(NSString*) s2 {
    return [[DifferenceEngine engine] similar:s1 other:s2];
}

- (BOOL) similar:(NSString*) s1
           other:(NSString*) s2 {
    if (s1 == nil || s2 == nil) {
        return NO;
    }
    
    if ([s1 length] > 4 && [s2 length] > 4) {
        if ([s1 rangeOfString:s2 options:NSCaseInsensitiveSearch].location != NSNotFound ||
            [s2 rangeOfString:s1 options:NSCaseInsensitiveSearch].location != NSNotFound) {
            return YES;
        }
    }
    
    NSInteger threshold = [s1 length] / 4;
    if (threshold == 0) {
        threshold = 1;
    }  
    
    NSInteger diff = [self editDistanceFrom:s1 to:s2 withThreshold:threshold];
    return (diff <= threshold);
}

- (NSString*) findClosestMatch:(NSString*) string
                       inArray:(NSArray*) array {
    NSString* lowercaseString = [string lowercaseString];
    
    NSInteger bestDistance = INT_MAX;
    NSString* bestMatch = nil;
    
    for (NSString* value in array) {
            int distance = [self editDistanceFrom:lowercaseString
                                               to:[value lowercaseString]];
            
        if (distance < bestDistance) {
            bestMatch = value;
            bestDistance = distance;
        }
    }
        
    if (bestMatch != nil &&
        [DifferenceEngine areSimilar:bestMatch other:string]) {
        return bestMatch;
    }
        
    return nil;
}

@end
