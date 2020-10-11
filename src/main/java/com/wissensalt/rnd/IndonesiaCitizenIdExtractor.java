package com.wissensalt.rnd;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BlockType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.wissensalt.rnd.IndonesiaCitizenIdKey.*;

/**
 * @author : <a href="mailto:wissensalt@gmail.com">Achmad Fauzi</a>
 * @since : 2020-10-11
 **/
public class IndonesiaCitizenIdExtractor {

    private IndonesiaCitizenIdKey indonesiaCitizenIdKey;

    private static final String COLON = ":";

    public IndonesiaCitizenId retrieve(String fileName, String bucketName, List<Block> blocks) {
//        IndonesiaCitizenId indonesiaCitizenId = new IndonesiaCitizenId();
//        indonesiaCitizenId.setFileName(fileName);
//        indonesiaCitizenId.setBucketName(bucketName);

        if (CollectionUtils.isEmpty(blocks)) {
            return null;
        }

        indonesiaCitizenIdKey = new IndonesiaCitizenIdKey();
        indonesiaCitizenIdKey.initOrder();

        for (String key : indonesiaCitizenIdKey.ARRAY_KEY) {
            for (Block block : blocks) {
                if (
                        !block.getBlockType().equals(BlockType.PAGE.toString())
                        && !block.getBlockType().equals(BlockType.KEY_VALUE_SET.toString())
                        && !block.getBlockType().equals(BlockType.TABLE.toString())
                        && !block.getBlockType().equals(BlockType.CELL.toString())
                        && !StringUtils.isEmpty(block.getText())
                ) {
                    setProvince(block);
                    setCityOrDistrict(block);
                    setFromKeyValueBlock(block, key);
                    if (!StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(key))) {
                        break;
                    }
                }
            }

            if (!StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(key))) {
                continue;
            }
        }


        return null;
    }

    public IndonesiaCitizenId retrieve2(String fileName, String bucketName, List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getBlockType().equals(BlockType.PAGE.toString())
                    && block.getBlockType().equals(BlockType.KEY_VALUE_SET.toString())
                    && block.getBlockType().equals(BlockType.TABLE.toString())
                    && block.getBlockType().equals(BlockType.CELL.toString())) {
                blocks.remove(block);
            }
        }

        IndonesiaCitizenId indonesiaCitizenId = new IndonesiaCitizenId();
        indonesiaCitizenId.setFileName(fileName);
        indonesiaCitizenId.setBucketName(bucketName);

        if (CollectionUtils.isEmpty(blocks)) {
            return null;
        }

        String key = null;
        String separator = null;
        for (Block block : blocks) {
            if (
                    !block.getBlockType().equals(BlockType.PAGE.toString())
                            && !block.getBlockType().equals(BlockType.KEY_VALUE_SET.toString())
                            && !block.getBlockType().equals(BlockType.TABLE.toString())
                            && !block.getBlockType().equals(BlockType.CELL.toString())
                            && !StringUtils.isEmpty(block.getText())
            ) {
                setProvince2(indonesiaCitizenId, block);
                setCityOrDistrict2(indonesiaCitizenId, block);
                setFromKeyValue2(indonesiaCitizenId, block, key, separator);
            }
        }


        return null;
    }

    private void setFromKeyValue2(IndonesiaCitizenId indonesiaCitizenId, Block block, String key, String separator) {
        indonesiaCitizenId.
    }

    private void setProvince2(IndonesiaCitizenId indonesiaCitizenId, Block block) {
        if (!StringUtils.isEmpty(indonesiaCitizenId.getProvince())) {
            return;
        }

        if (block.getText().contains(PROVINCE)) {
            indonesiaCitizenId.setProvince(block.getText());
        }
    }

    private void setCityOrDistrict2(IndonesiaCitizenId indonesiaCitizenId, Block block) {
        if (!StringUtils.isEmpty(indonesiaCitizenId.getCityOrDistrict())) {
            return;
        }

        if (block.getText().contains(CITY) || block.getText().contains(DISTRICT)) {
            indonesiaCitizenId.setCityOrDistrict(block.getText());
        }
    }

    private void setProvince(Block block) {
        if (!StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(PROVINCE))) {
            return;
        }

        if (block.getText().contains(PROVINCE)) {
            indonesiaCitizenIdKey.orderMap.put(PROVINCE, block.getText());
        }
    }

    private void setCityOrDistrict(Block block) {
        if (!StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(CITY)) ||
                !StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(DISTRICT))) {
            return;
        }

        if (block.getText().contains(CITY) || block.getText().contains(DISTRICT)) {
            indonesiaCitizenIdKey.orderMap.put(CITY, block.getText());
            indonesiaCitizenIdKey.orderMap.put(DISTRICT, block.getText());
        }
    }

    /**
     * Retrieve value from key value {@link Block} separated by @{link {@link IndonesiaCitizenIdExtractor#COLON}}
     *
     * Case 1 :
     * <ol>
     *     <li>Key is present</li>
     *     <li>Separator is present</li>
     *     <li>Value is present</li>
     * </ol>
     *
     * Case 2 :
     * <ol>
     *     <li>Key is not present</li>
     *     <li>Separator is not present</li>
     *     <li>Value is present</li>
     * </ol>
     *
     * @see Block
     * @see IndonesiaCitizenId
     */
    private void setFromKeyValueBlock(Block block, String key) {
        if (!StringUtils.isEmpty(indonesiaCitizenIdKey.orderMap.get(key))) {
            return;
        }

        if (indonesiaCitizenIdKey.containsPreviousKey(key, block.getText())) {
            return;
        }

        if (!indonesiaCitizenIdKey.previousKeyIsEmpty(key) && indonesiaCitizenIdKey.nextKeyIsEmpty(key)) {
            if (block.getText().contains(key) && block.getText().contains(COLON)) {
                String [] slicedStringBlock = block.getText().split(COLON);
                if (slicedStringBlock.length == 2) {
                    indonesiaCitizenIdKey.orderMap.put(key, slicedStringBlock[slicedStringBlock.length - 1]);
                }
            }

            if (!block.getText().contains(key)
                    && !block.getText().contains(COLON)
                    && !StringUtils.isEmpty(block.getText())
            ) {
                indonesiaCitizenIdKey.orderMap.put(key, block.getText());
            }

        }
    }


}
