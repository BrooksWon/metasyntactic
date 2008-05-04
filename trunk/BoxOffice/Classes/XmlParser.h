//
//  XmlParser.h
//  BoxOffice
//
//  Created by Cyrus Najmabadi on 5/2/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "XmlElement.h"

@interface XmlParser : NSObject {
    NSMutableArray/*<NSMutableArray>*/* elementsStack;
    NSMutableArray/*<NSMutableString>*/* stringBufferStack;
    NSMutableArray/*<NSDictionary>*/* attributesStack;
}

@property (retain) NSMutableArray* elementsStack;
@property (retain) NSMutableArray* stringBufferStack;
@property (retain) NSMutableArray* attributesStack;

- (id) initWithData:(NSData*) data;
- (void) dealloc;

+ (XmlElement*) parse:(NSData*) data;

@end
