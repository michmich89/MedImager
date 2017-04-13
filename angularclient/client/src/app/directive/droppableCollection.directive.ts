import { Directive, ElementRef, HostListener, Input } from '@angular/core';

import { CollectionService } from '../service/collection.service';
import { AppModule } from '../app.module';


@Directive({ 
    selector: '[droppableCollection]' 
})

export class DroppableCollectionDirective {
    constructor(private el: ElementRef, private collectionService: CollectionService) {
    }

    @HostListener('dragenter', ['$event']) handleDragEnter(event) {
        event.preventDefault();
	    return true;
    }

    @HostListener('dragover', ['$event']) handleDragOver(event) {
        event.preventDefault();
	    return false;
    }

    @HostListener('drop', ['$event']) handleDrop(event) {	
        event.preventDefault();
        var examinationID = event.dataTransfer.getData("examinationID");
        var imageIndex = event.dataTransfer.getData("imageIndex");
        var targetCollectionName = event.target.innerHTML;
        var targetCollectionID = event.target.getAttribute("data-id");
        var type = event.dataTransfer.getData("sourceType");
        if (type == "draggableImage") {
            //TODO: actually make this add to collection
            alert("image "+ examinationID + "/" + imageIndex + " added to collection (id: " + targetCollectionID + ")");
            this.collectionService.addImage(examinationID, imageIndex, targetCollectionID);
            
        }

    }
}