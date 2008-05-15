//
//  Utilities.h
//  BoxOffice
//
//  Created by Cyrus Najmabadi on 5/8/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

@interface Utilities : NSObject {
    
}

+ (BOOL) isNilOrEmpty:(NSString*) string;

+ (id) findSmallestElementInArray:(NSArray*) array 
                    usingFunction:(NSInteger(*)(id, id, void *)) comparator
                          context:(void*) context;

@end
