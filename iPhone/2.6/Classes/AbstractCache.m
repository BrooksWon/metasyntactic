// Copyright 2008 Cyrus Najmabadi
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import "AbstractCache.h"

#import "FileUtilities.h"

@interface AbstractCache()
@property (retain) NowPlayingModel* model;
@property (retain) NSLock* gate;
@end


@implementation AbstractCache

@synthesize model;
@synthesize gate;

- (void) dealloc {
    self.model = nil;
    self.gate = nil;

    [super dealloc];
}


- (id) initWithModel:(NowPlayingModel*) model_ {
    if (self = [super init]) {
        self.model = model_;
        self.gate = [[[NSRecursiveLock alloc] init] autorelease];
    }

    return self;
}


- (NSSet*) cachedDirectoriesToClear {
    return [NSSet set];
}


- (void) clearStaleData {
    NSSet* cachedDirectoriesToClear = self.cachedDirectoriesToClear;

    for (NSString* path in cachedDirectoriesToClear) {
        NSDate* lastModifiedDate = [FileUtilities modificationDate:path];
        if (lastModifiedDate != nil) {
            if (ABS(lastModifiedDate.timeIntervalSinceNow) > CACHE_LIMIT) {
                [FileUtilities removeItem:path];
            }
        }
    }
}


@end